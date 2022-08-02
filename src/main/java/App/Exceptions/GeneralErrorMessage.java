package App.Exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
@AllArgsConstructor
public class GeneralErrorMessage {
    private String errorCode;
    private String msg;
}
