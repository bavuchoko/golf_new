package pjs.golf.app.game.unit;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pjs.golf.app.account.entity.Account;
import pjs.golf.app.account.service.AccountService;
import pjs.golf.app.game.dto.GameRequestDto;
import pjs.golf.app.game.dto.GameStatus;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.game.repository.GameJpaRepository;
import pjs.golf.app.game.repository.querydsl.GameQuerydslSupport;
import pjs.golf.app.game.service.impl.GameServiceImpl;
import pjs.golf.app.sheet.service.SheetService;
import pjs.golf.common.exception.NoSuchDataException;
import pjs.golf.common.exception.PermissionLimitedCustomException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameServiceUnitTest {
    @Mock
    private GameJpaRepository gameJpaRepository;

    @Mock
    private GameQuerydslSupport gameQuerydslSupport;

    @Mock
    private AccountService accountService;

    @Mock
    private SheetService sheetService;


    @InjectMocks
    private GameServiceImpl gameService;

    private Game game;
    private Account account;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .id(1L)
                .username("testUser")
                .build();

        game = Game.builder()
                .id(1L)
                .players(List.of(account))
                .host(account)
                .status(GameStatus.OPEN)
                .build();
    }

    @Test
    void 경기조회_성공() {
        when(gameJpaRepository.findById(anyLong())).thenReturn(Optional.of(game));

        Game foundGame = gameService.getGameInfo(1L);

        assertNotNull(foundGame);
        assertEquals(1L, foundGame.getId());
        verify(gameJpaRepository, times(1)).findById(1L);
    }

    @Test
    void 경기조회_해당내역없음() {
        when(gameJpaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchDataException.class, () -> gameService.getGameInfo(1L));
        verify(gameJpaRepository, times(1)).findById(1L);
    }

    @Test
    void 경기종료_성공() {
        when(gameJpaRepository.findById(anyLong())).thenReturn(Optional.of(game));

        gameService.endGame(1L, account);

        assertEquals(GameStatus.END, game.getStatus());
        verify(gameJpaRepository, times(1)).findById(1L);
    }

    @Test
    void 경기종료_실패_권한없음() {
        Account anotherAccount = Account.builder()
                .id(2L)
                .username("anotherUser")
                .build();

        when(gameJpaRepository.findById(anyLong())).thenReturn(Optional.of(game));

        assertThrows(PermissionLimitedCustomException.class, () -> gameService.endGame(1L, anotherAccount));
    }

    @Test
    void 경기삭제_성공() {
        // 모킹할 게임과 계정 객체 생성
        Account hostAccount = Account.builder()
                .id(1L)
                .username("hostUser")
                .build();

        List<Account> players = mock(List.class);
        Game game = mock(Game.class);

        when(game.getHost()).thenReturn(hostAccount);
        when(game.getPlayers()).thenReturn(players);
        when(gameJpaRepository.findById(anyLong())).thenReturn(Optional.of(game));

        Account requestAccount = hostAccount;

        gameService.removeGame(1L, requestAccount);

        // 게임의 플레이어 리스트가 clear() 호출되었는지 검증
        verify(game, times(1)).getPlayers();
        verify(players, times(1)).clear();
        verify(gameJpaRepository, times(1)).delete(game);
    }

    @Test
    void 경기삭제_실패_권한없음() {
        Account anotherAccount = Account.builder()
                .id(2L)
                .username("anotherUser")
                .build();

        when(gameJpaRepository.findById(anyLong())).thenReturn(Optional.of(game));

        assertThrows(PermissionLimitedCustomException.class, () -> gameService.removeGame(1L, anotherAccount));
    }

    @Test
    void 경기생성_성공() {
        GameRequestDto gameRequestDto = GameRequestDto.builder()
                .names(new String[0])
                .build();
        when(gameJpaRepository.save(any(Game.class))).thenReturn(game);

        Game createdGame = gameService.createGame(gameRequestDto, account);

        assertNotNull(createdGame);
        assertEquals(game.getId(), createdGame.getId());
        verify(gameJpaRepository, times(1)).save(any(Game.class));
    }
}
