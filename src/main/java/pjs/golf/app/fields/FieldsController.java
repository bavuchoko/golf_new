package pjs.golf.app.fields;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/fields", produces = "application/json;charset=UTF-8")
@RequiredArgsConstructor
public class FieldsController {
}
