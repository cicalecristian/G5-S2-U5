package cristiancicale.G5S2U5.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class DipendentePayload {

    private String username;
    private String nome;
    private String cognome;
    private String email;
}
