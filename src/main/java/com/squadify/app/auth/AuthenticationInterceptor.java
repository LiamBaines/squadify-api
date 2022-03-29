package com.squadify.app.auth;

import com.squadify.app.squad.Squad;
import com.squadify.app.squad.SquadDao;
import com.squadify.app.user.SquadifyUser;
import com.squadify.app.user.SquadifyUserDao;
import com.wrapper.spotify.exceptions.detailed.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.squadify.app.SquadifyApiConfig.BASE_URL;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private SquadDao squadDao;

    @Autowired
    private SquadifyUserDao squadifyUserDao;

    @Autowired
    private HttpSession session;

    @Autowired
    private RequestContext requestContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws BadRequestException, UnauthorisedException {
        boolean isMethodHandler = handler.getClass().equals(HandlerMethod.class);
        if (!isMethodHandler) {
            return true;
        }
        boolean isFromSquadifyClient = isFromSquadifyClient(request);
        boolean urlContainsSquadKey = urlContainsSquadKey(request);
        boolean squadOwnershipRequired = squadOwnershipRequired(handler);


        if (isFromSquadifyClient(request)) {
            validateUserExists();

            if (urlContainsSquadKey(request)) {
                validateSquadExists(request);

                if (squadOwnershipRequired(handler)) {
                    validateUserOwnsSquad();
                }

            }

        }

        return true;
    }

    private void validateUserOwnsSquad() throws UnauthorisedException {
        SquadifyUser user = requestContext.getUser();
        SquadifyUser owner = requestContext.getSquad().getOwner();
        if (!user.equals(owner)) {
            throw new UnauthorisedException("You must be own a squad to perform this action.");
        }
    }

    private static boolean squadOwnershipRequired(Object handler) {
        return getMethodAnnotations(handler).contains(SquadOwnerOnly.class);
    }

    private static boolean isFromSquadifyClient(HttpServletRequest request) {
        return (BASE_URL + ":3000").equals(request.getHeader("origin"));
    }

    private static boolean urlContainsSquadKey(HttpServletRequest request) {
        return nonNull(getSquadKeyFromUrl(request));
    }

    private static List<Class<? extends Annotation>> getMethodAnnotations(Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        return Arrays.stream(handlerMethod.getMethod().getAnnotations())
                .map(Annotation::annotationType)
                .collect(toList());
    }

    private void validateUserExists() throws UnauthorisedException {
        String username = (String) session.getAttribute("userId");
        SquadifyUser user = squadifyUserDao.findByUsername(username).orElseThrow(() -> new UnauthorisedException("User doesn't exist"));
        requestContext.setUser(user);
    }

    private void validateSquadExists(HttpServletRequest request) throws BadRequestException {
        String squadKey = getSquadKeyFromUrl(request);
        Squad squad = squadDao.findBySquadKey(squadKey).orElseThrow(() -> new BadRequestException(String.format("Squad with key %s doesn't exist", squadKey)));
        requestContext.setSquad(squad);
    }

    private static String getSquadKeyFromUrl(HttpServletRequest request) {
        Map<String, String> pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        return pathVariables.get("squadKey");
    }

}
