package com.squadify.app.playlist;

import com.squadify.app.api.SpotifyApiFactory;
import com.squadify.app.auth.AuthService;
import com.squadify.app.squad.Squad;
import com.squadify.app.squad.SquadService;
import com.squadify.app.user.SquadifyUser;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.requests.data.follow.FollowPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.CreatePlaylistRequest;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static com.squadify.app.core.SquadifyUtils.printRepresentationPercentages;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class PlaylistCreator {

    private final AuthService authService;
    private final CommonTrackFinder commonTrackFinder;
    private final SpotifyApiFactory spotifyApiFactory;
    private final SquadService squadService;
    private final TrackFetcherFactory trackFetcherFactory;

    Playlist createPlaylist(Squad squad) throws ParseException, SpotifyWebApiException, IOException {
        Map<SquadifyUser, Set<String>> squadsTopTracks = getSquadsTopTracks(squad);
        String[] uris = commonTrackFinder.findCommonTracks(squadsTopTracks);
        printRepresentationPercentages(squad, squadsTopTracks, uris);
        Playlist playlist = createPlaylistFromTracks(squad, uris);

//        for (SquadifyUser user : squad.getAllUsers()) {
////            followPlaylist(user, playlist);
////        }

        followPlaylist(squad.getOwner(), playlist);

        squadService.addPlaylistUrlToSquad(squad, playlist.getId());

        return playlist;
    }

    private Map<SquadifyUser, Set<String>> getSquadsTopTracks(Squad squad) throws ParseException, SpotifyWebApiException, IOException {

        Map<SquadifyUser, Set<String>> squadsTopTracks = new LinkedHashMap<>();

        for (SquadifyUser user : squad.getAllUsers()) {
            TrackFetcher trackFetcher = trackFetcherFactory.trackFetcherFrom(user);
            squadsTopTracks.put(user, trackFetcher.getTracksForUser());
        }
        return squadsTopTracks;
    }

    private Playlist createPlaylistFromTracks(Squad squad, String[] uris) throws ParseException, SpotifyWebApiException, IOException {
        authService.refreshCredentialsForUser(squad.getOwner());
        Playlist playlist = createEmptyPlaylist(squad);
        updatePlaylistImage(squad, playlist);
        addTracksToPlaylist(squad.getOwner(), playlist, uris);
        return playlist;
    }

    private void updatePlaylistImage(Squad squad, Playlist playlist) throws ParseException, SpotifyWebApiException, IOException {
        spotifyApiFactory.spotifyApiFrom(squad.getOwner())
                .uploadCustomPlaylistCoverImage(playlist.getId())
                .image_data("iVBORw0KGgoAAAANSUhEUgAAAZAAAAGQCAYAAACAvzbMAAAACXBIWXMAAA7DAAAOwwHHb6hkAAAAGXRFWHRTb2Z0d2FyZQB3d3cuaW5rc2NhcGUub3Jnm+48GgAAIABJREFUeJzt3XmYFdWdxvH39sK+NN0sYssuoIIIasQlKi5BAZ3RR3EZBo2PTBKSMShxmSwjPqNCYMZgIIILY5SgEwcTB0WNikqiBgUUFRBZtBGQfWmggW7ovjV/FI0gdN+qc+vec6vq+3meekyg7rm/W9xbby3nnEpIcgQAgE95tgsAAIQTAQIAMEKAAACMECAAACMECADACAECADBCgAAAjBAgAAAjBAgAwAgBAgAwQoAAAIwQIAAAIwQIAMAIAQIAMEKAAACMECAAACMECADACAECADBCgAAAjBAgAAAjBAgAwAgBAgAwQoAAAIwQIAAAIwQIAMAIAQIAMEKAAACMECAAACMECADACAECADBCgAAAjBAgAAAjBAgAwAgBAgAwQoAAAIwQIAAAIwQIAMAIAQIAMEKAAACMECAAACMECADACAECADBCgAAAjBAgAAAjBAgAwAgBAgAwQoAAAIwQIAAAIwQIAMAIAQIAMEKAAACMECAAACMECADACAECADBCgAAAjBAgAAAjBAgAwAgBAgAwQoAAAIwQIAAAIwQIAMAIAQIAMEKAAACMECAAACMECADACAECADBCgAAAjBAgAAAjBAgAwAgBAgAwQoAAAIwQIAAAIwQIAMAIAQIAMEKAAACMECAAACMFtgsAkJ6+ffvqhhtuSLneH//4R3388cdZqCh9JSUlat++vUpKSlRSUqKKigpt2rRJy5cvV2Vlpe3ycBABAoRcr169dM8996Rc79NPP83pADn77LM1dOhQXXrppTr11FOVSCSOWufAgQNavHix3njjDT3//PNauHChhUpxOIeFhSW8y7Bhwxwv/umf/sl6rcdaevfu7bz99tuePsO3ffjhh875559v/TPEdeEeCABrbr75Zi1YsEADBgwwev3pp5+u2bNnq2XLlsEWBk+4hAXAiu9///t68sknj3mpyo/7779fO3fuDKgq+MEZCICsO/XUUzVlypS0w2PVqlWaPHlyQFXBLwIEQFYVFhZq5syZaty4cdpt/fSnP1VVVVUAVcEEAQIgq2688Ub17Nkz7XZeeuklvfrqqwFUBFPcAwGQVXfeeaev9ZctW6a1a9equLhY/fr1U35+vqqqqvSzn/0sQxXCKwIEwDHl5eXpyiuv1D/8wz+oY8eO2rhxo1555RX97//+r2pqaoza7NGjh0499VRP6y5ZskQ333yzPvroo0N/VlpaqoceekhlZWVauXKlUQ0IlvW+xCwsLOZLJsaBdOjQwfn73/9+zHYWLFjgdOjQwajWf/mXf/FU69q1a52SkpJjtpGXl+c0btzY+nZnYRwIgG9p1qyZ3nrrLZ1zzjnH/PszzzxTb775ppo1a+a77bPPPtvTev/5n/+pbdu2HfPvksmk9u3b5/u9ETwCBMARbrvtNp144on1rtO9e3f95Cc/8d12u3btPK3HzfFwIEAAHOHiiy/2tN4ll1ziu+2SkpKU6ySTSX3xxRe+20b2ESAAjtCkSZNA1ztc8+bNU65TUVGhZDLpu21kHwEC4AjLli3ztN5nn33mu20vI88Jj/AgQAAcYerUqSm76dbU1Gjq1KlZqgi5igABcIQPP/xQI0eOVHV19TH/vrq6WiNHjtSiRYuyXBlyDQEC4ChPPPGEzjrrLP3pT3/Snj17JElVVVV65ZVXdN555+mJJ56wXCFyASPRARzTokWLdO211yovL08tW7bU7t276zwrQTwRIADqlUwmtWPHDttlIAdxCQsAYIQAAQAY4RLWQYlEQp07d1bnzp1VUlKiRCKhqqoq7d69W7t27dIXX3yh8vLyjNfRtm1b9evXT126dFHLli2Vn5+vyspKrV+/XitWrNCnn37KdWiPEomEunfvrjPOOEPt2rVTkyZNVFNTo+3bt2v16tVasmSJNmzYYLvMQ9q1a6devXqpS5cuatWqlQoLC1VRUaFt27ZpzZo1WrhwoSorK22XCRwS6wBJJBIaNGiQbrrpJl1++eVq2bJlvet/+eWX+vDDDzVr1iz96U9/CuzH3L59e40YMULXXnut+vTpU++6O3fu1EsvvaRJkyZpwYIFh/78Jz/5iS688MKU7zVq1Kic2mlmQqdOnTRy5EgNGzZMJ5xwQp3rOY6jRYsWaerUqXrqqaesBPPJJ5+sW2+9VUOGDNFJJ51U77qVlZX64IMPNGvWLE2fPr3OyQZzRdeuXY/6swYNGqR8XV5e3jFf+21r167VgQMHjGpDcKxPCWxjOe2005wPPvjA09TSx7J9+3Zn/PjxTvPmzY1rKCoqciZPnuzs27fP9/snk0ln+vTpTrt27RxJzlNPPeXpdd27d/dU28KFC1O2VVlZ6evzrlixImWbmzdvNt6ezZs3dyZOnOhUVlZ62haH++ijj5yTTjrpUFu33npryte8//77xrWefPLJzuzZs51kMum7VsdxnN27dzu/+tWvnMLCwsCnc3/00Uc9tdenT58620gkEkafy4+ePXse8Z5jxozx/No77rjD+N8ukUg4y5Ytq7f9LVu2xGXKeesFZH256aabnKqqqjS/vq5169Y511xzje8aBg4c6Hz99ddpv/+OHTucK6+8MvYBcu655zpffvmlp21Qlz179jjDhw93JDlTp05Nub5JgCQSCWfMmDHO/v3706q11rx585zbb7/d07pRD5BWrVo5O3fu9PTasrIyp6CgwOi7dsUVV6Rs/8EHH7S+n8vSYr2ArC7Dhg0zPuqry7hx43zV8KMf/ciprq4O7P2rq6ud1atXe1o3igFy1VVXGZ3FHUsymXR++MMfOvPnz0+5rt8AadSokfPnP/85kDoP5zWMoh4gkpwHH3zQ8+uvu+46o33I22+/XW+7+/fvd0pLS63v67K0WC8ga0u3bt2cvXv3pvm1PdLy5cudRo0aea7hhz/8YaDv71fUAmTgwIHOgQMHgtg0hySTSU8B7ydA8vLynOeffz7QOv2KQ4C0bt3aqaio8PR6kzPI008/PWW7zzzzjPV9XbaWWHXjve+++9S4cePA2ksmkxoxYoTnm+kDBw7U7373u8DeP+5OPPFEPffccyooCLYvSCKRUH5+fqBtjhkzRtdcc02gbeJoW7du1aOPPupp3f79++u8887z1f7PfvazlOv89re/9dVmmMUmQIqKijz/gGfMmKHzzjtPxcXF6tixowYPHqynn35ae/fuPWK9xx57TO+8846nNlu1aqUnn3wy8J1dXCUSCT3++OMqKiqyXUpK/fr1089//nPbZcTGf/3Xf3l+5O3o0aM9t9uhQwcNHTq03nXmzZun+fPne24z7GKzN7v66qs9nX1MmjRJo0aNOvT/d+zYobVr1+rVV1/V3XffrTvvvFOjRo3Spk2b9G//9m+e33/s2LEqLS31Xfe+fftUVlamLVu2qKSkRF26dFHTpk19txM1N9xwgy666CJfr6mqqtKcOXO0cuVKbd26VW3atFHv3r11/vnne+peamr8+PEqLCz09ZolS5Zo+vTp+vzzz7Vhwwa1aNFCXbt21SWXXKIhQ4Z4ejBTXG3cuFHTpk3TbbfdlnLdf/zHf1S3bt08PQHxpz/9acp/xzidfdSyfh0tG8uvf/1rT9dFL7300pRtnXbaac6FF17o+b27dOniu9fNm2++6VxxxRVOXl7eEW0VFhY6AwYMcGbMmGHUkycK90ASiYTz6aefev7M1dXVzrhx4w51ef72UlRU5Pzyl790ysvLPbfpON6uoZ911lm+2tywYYMzZMgQJ5FI1NlmUVGRc++99/r+94/DPZDa5YQTTvDcnXvSpEkpt0mLFi1Sfj/WrFlj3LMrrEtszkA6duzoab3evXtrzpw59a7zySef+HrvUaNGeT4Cra6u1p133lnnkcyBAwc0d+5czZ07V7/73e/03HPPef5sUXHxxRfr1FNP9bTu9u3bdf3119f7b1peXq4HH3xQM2fO1EsvvaQePXoEVaqGDx/ued1FixZpyJAhKQd6lpeX6z/+4z80d+5cvfzyy2rWrFm6ZWbE+PHjj/qzW2+9Va1bt673dVVVVXr44YdTtr99+/Y6/27dunV6+umn9YMf/CBlO7fccovGjBlT74SRI0aMSDnQeMqUKbGcJcJ6imVjmT59uqejkR07djhXX311YO+bSCSctWvXenpvx3GcG264wVf7nTp1csrKyjy3H4UzkMmTJ3v6rMlk0hkyZIivGrt06eJs3rzZU/upzkASiYSzZs0aT21t27bN6dy5s+/vl5cxCbWyeQZS17J06dKU7e7YsSOQ356fM/977rmnznYKCgpSdpPfs2ePU1xcHEjdYVpicxP9q6++8rReUVGR/vznP2vhwoUaPXq0pykV6tOvX796p9M43OOPP64//vGPvtr/6quvNHTo0Fgd+QwePNjTetOmTdPLL7/sq+2ysjLde++9JmUdpUOHDurQoYOndW+//XatXr3a93vMnj1br7zyiu/XxUFZWZmeeeYZT+vedtttdd4Hu/baa9WpU6d6X/+HP/yh3jOiqIpNgPz1r3/1tf4ZZ5yhhx56SF988YWWLl2q8ePHq3///r7ft1+/fp7W27lzp+655x7f7UvSwoUL9cILLxi9NmxatGihLl26eFrX9Ibmk08+qWQyafTaw/Xq1cvTel9//bWeffZZ4/f5y1/+YvzaqBs3blzK57tLUmlpqa677rpj/l2qrruO42jSpElG9YVdbALk7bff9nwW8m2nnHKK7r77br3//vtavHixbr31VuXledt0Xq/Vz5w5M63Zfr/dxTgXBTG24pRTTlEikUi53scff6ylS5cavcf+/fvlOI7Raw/n9ex1zpw5nnZy8G/FihV67rnnPK17rC69F1xwgc4888x6X/fGG2/os88+M6ov7GITIDU1Nbr//vvTbqd3796aNm2a5s2b5+lma9u2bT21+/7776dbWs5r1apV2m20a9fO03rLly9P+73S1aJFC0/rmVy6gndjx471dEbZr18/XXzxxUf8GQMH6xebAJGk3//+94FdLz7rrLP0zjvvpLxE5bW//po1a4IoK2cVFBQEMujP6/b8+uuv036vdHntHbVx48YMVxJvS5cu9XyJ9/CzkJ49e+qKK66od/3ly5fr1VdfTau+MItVgCSTSQ0dOlSvv/56IO21bdtWL7zwQr07Na8D1LLxsCqb2rVr5+nSUyoNGzb0tF6QU9aY2r9/v6f12rRpk+FK8MADD3i6LDl48OBDz2W5/fbbU16qnjx5ciCXO8MqVgEiufcKBg0apF/+8pfas2dP2u116tRJ9913X51/7/U9TEaph8k555wTSDte7/XkwtiYiooKT+ul6uGD9H388ceaPXt2yvUSiYTuuOMOtWnTRjfffHO965aXl+vpp58OqsRQil2ASO6ZyNixY9WtWzdNmDAh7Sf0ff/731ejRo2O+Xe7d+/21Ea63YVz3YABAwJpx+tOuW/fvp47OmTKli1bPK3n5UmSSN8DDzzgab3hw4fr3//931OexU6bNs3z9zGqYhkgtTZt2qR77rlHHTt21GWXXaapU6caXTsvLi7Wueeee8y/Kysr89TGlVde6ft9Dxf07LFBys/P15AhQwJpa926dZ7WKy0t1fe+9z2j98jLywvkctuKFSs8rXfiiSdq4MCBxu9TXFxs/No4mT9/vqfL140bN045j1ZNTY0eeeSRoEoLrVgHSK3q6mq9/vrr+vGPf6wOHTro3HPP1WOPPebrect1DRhbtmyZp9cPGDDA99TStVq2bOl5cF2QCgsLPd1ruOaaa9S5c+dA3nP58uWex2iMGzfO8z2Tw40YMSKQs5fPP//cc62TJ082mpKkQ4cOvmaUjbsgemJK0v/93//Re04xDJAmTZrUe7TuOI7mzZunH/3oR+rfv7/n0aV1HQX66Z77hz/8wair68SJE60chebl5emUU06pd52CgoJApzLfu3evVq5c6Wndfv36HXM+pvp069ZN48aNMyntKOXl5froo488rdujRw89/vjjvs4ki4qK9OKLL3ruLgzp3Xff1dy5c9NuJ85ddw8XuwB58skn9fLLL3vaUS9atEgTJkzw1G5dvai++uorLVmyxFMbXbp00fPPP+85RPLy8vTrX/9at9xyi6f1M+Hqq6+u9+/Hjh2rvn37BvqefrpNjho1Sg899JCnHXO3bt00e/bsQMPYzyjxG2+8UbNnz/b079+rVy/97W9/C3zbxoHXeyF1+eijjzw/BygOrE/Ila1l5MiRhyY/W7FihdO/f/+Ur7nllls8TcZ21lln1dnGL37xC09t1Fq5cmXK6eL79u3rvP76677adZxgJ1N0HMfZuXOn07Vr16NeX1hY6EyYMMF3fV4mU7zgggt8t/vuu+86gwcPPmp6fElO06ZNndGjRzs7duzw1aaX6dy7du3q1NTU+Gp38+bNzgMPPOB0797dyc/PP6LOiy++2HnqqaeMpvKP22SK9S3vvfee7+1X66abbsrqfivHF+sFZGU5/fTTj3o+QE1NjfPEE08ccwcoyWnfvr3zySefpPxCbdmyxSksLKzzvUtKSpw9e/b4/qIuXbrUGTt2rPODH/zAGTp0qDNixAhn/Pjxzrx585xkMum7PcfxHiCvvfaa5za3b9/uTJgwwbnxxhudG264wbnvvvucVatWGdXn9ZnoCxYsMGp/3bp1zmuvveZMmzbNmTFjhvPOO+94fm7Et3l9pvasWbOM2nccx6mqqnJWr17tbNu2zfjfvBYB8s0yaNAgo224YcMGp2HDhtb3Zzm0WC8g40vLli1T7tAWLFjgTJkyxfnVr37lPPzww84LL7zg7N2719OX6uGHH05Zw7333mv0hQ2a1wB57LHHrNTnNUD8TGOeKV4DpGfPnk5VVZXtcgmQby0mByH33nuv9f1Zji3WC8j48vzzz/v+onhVUVHhlJaWpqyhQYMGzscff5yxOrzyGiDXX3994O+9e/fulOt4DRBJzrPPPht4jX54DRBJzpgxY6zW6jgEyLeXq666ytf2q6ysrPOplnFdIn8TvWHDhoFM4leXu+66y9PYkf379+uqq67S5s2bM1ZLkF555RXt3LkzsPYWLlwY+JTXP/7xj41n3K1PULPxHu7+++/nuR05ZtasWfr00089r/8///M/2rRpUwYrCp/IB0hVVZWGDBmi6dOnB972xIkTNXXqVM/rr169WpdffnlGJs/zM2bFi927dwfWZ3779u0aPnx44DPklpeXa9CgQZ679Xqxa9cuDR48OJDngRwumUzquuuu05tvvhlou9u2bdOLL74YaJtx4TiOHnzwQc/r03X32KyfBmVrGTZsmLNx40Zfp63HUlVV5YwaNcq4ji5dujjz589Puw7HcZx9+/Y5d999tzNp0iRP63u9hCW5PaneeOONtOqrqKhwzj77bEeSc95556Vc388lrNqluLjY103/uqxdu9bp16+fI8mprq5Oub6fS1i1S6NGjZwnnngi7Vodx3FmzZrllJaWOrfddpun9bmEdfSSl5fnLFu2LGVNc+fOtbrvyuHFegFZXVq0aOH8/Oc/d9avX+/pR3K4mpoa58UXX3R69uyZdh0FBQXO3Xff7WzZssV3HY7jOOXl5c7EiRMP9SD7zW9+4+l1fgKkdnvNnTvXqMbFixc7vXr1OtTWcccdl/I1JgEiuc8f/+d//mfnyy+/9F1nVVWVM2XKFKdly5aH2stUgNQuV155pbNkyRLftTqO2+Hj8G7eBEh6v8OysrKUNV199dVZqylMS4FiZteuXRo3bpwmTJigAQMGaPDgwfrud7+rk08++ZjTsm/dulWLFy/WnDlzNHPmzMAul1RXV2vChAl65JFHNGzYMA0dOlQXXHBBvdO/b9q0Se+9955mz56tmTNnZmUit127dumSSy7RL37xC911112ensexbt06/fa3v9Ujjzyiffv2HfrzTZs2ady4cfVOE2I6Q7LjOJoxY4aeffZZDRw4UNdcc40uu+yyep9JvnTpUs2aNUtTpkw56j7WhAkTUk5nsnbtWqNaJemll17Syy+/rCFDhuj666/X4MGD671Xt3btWr3xxhuaMWOG5s6de8Q9mg8//NDTiHuvT8177bXXPD1ewOR+3n//93+nfMhaZWWl73ZNeZlmp6ysjMuEdUjITRLIfWJeq1atVFBQoKqqKm3bti2rs20WFhaqV69eKi0tVVFRkSorK7V9+3Zt27ZNmzdvrvfeyW9+8xvdcccdKd+jR48exiFYXFys4cOH69JLL9Vpp52m1q1bK5FIaOvWrdqwYYPee+89vf7665oz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")
                .build()
                .execute();
    }

    private Playlist createEmptyPlaylist(Squad squad) throws ParseException, SpotifyWebApiException, IOException {
        SpotifyApi ownerSpotifyApi = spotifyApiFactory.spotifyApiFrom(squad.getOwner());
        CreatePlaylistRequest createPlaylistRequest = ownerSpotifyApi
                .createPlaylist(squad.getOwner().getUsername(), squad.getName())
                .description(getDescription(squad))
                .build();

        return createPlaylistRequest.execute();
    }

    private String getDescription(Squad squad) {
        List<String> firstNames = squad.getAllUsers().stream()
                .map(SquadifyUser::getFirstName)
                .collect(toList());

        String commaSeparatedFirstNames = String.join(", ", firstNames);
        return "A Squadify playlist made for "
                + commaSeparatedFirstNames.substring(0, commaSeparatedFirstNames.lastIndexOf(","))
                + " and " + firstNames.get(firstNames.size() - 1);
    }

    private void addTracksToPlaylist(SquadifyUser owner, Playlist playlist, String[] uris) throws ParseException, SpotifyWebApiException, IOException {
        for (String[] subArray : subArraysFrom(uris, 20)) {
            String playlistId = playlist.getId();
            AddItemsToPlaylistRequest addItemsToPlaylistRequest = spotifyApiFactory.spotifyApiFrom(owner)
                    .addItemsToPlaylist(playlistId, subArray)
                    .build();
            addItemsToPlaylistRequest.execute();
        }
    }

    private List<String[]> subArraysFrom(String[] array, int subArraySize) {
        List<String[]> subArrays = new ArrayList<>();
        for (int i = 0; i < array.length; i += subArraySize) {
            String[] subArray = Arrays.copyOfRange(array, i, Math.min(array.length, i + subArraySize));
            subArrays.add(subArray);
        }
        return subArrays;
    }

    private void followPlaylist(SquadifyUser user, Playlist playlist) throws ParseException, SpotifyWebApiException, IOException {
        FollowPlaylistRequest request = spotifyApiFactory.spotifyApiFrom(user).followPlaylist(playlist.getId(), true)
                .build();
        request.execute();
    }

}
