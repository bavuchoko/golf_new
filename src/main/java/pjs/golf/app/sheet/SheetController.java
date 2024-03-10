package pjs.golf.app.sheet;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/sheet", produces = "application/json;charset=UTF-8")
@RequiredArgsConstructor
public class SheetController {
}
