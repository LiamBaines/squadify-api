package com.squadify.app.squad;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.squadify.app.core.SquadifyController;
import com.squadify.app.model.SquadDto;
import com.squadify.app.model.UpdateSquadRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/squads")
public class SquadController extends SquadifyController {

    private final SquadService squadService;

    @Autowired
    public SquadController(SquadService squadService) {
        super(null, null);
        this.squadService = squadService;
    }

    @PostMapping
    public SquadDto createSquad()  {
        return squadService.createSquad();
    }

    @PutMapping("/{squadId}")
    public void updateSquad(@PathVariable String squadId, @RequestBody String json) throws JsonProcessingException {
        UpdateSquadRequestDto request = objectMapper.readValue(json, UpdateSquadRequestDto.class);
        squadService.updateSquad(squadId, request);
    }

    @DeleteMapping("/{squadId}")
    public void deleteSquad(@PathVariable String squadId) {
        squadService.deleteSquad(squadId);
    }

}
