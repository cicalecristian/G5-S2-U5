package cristiancicale.G5S2U5.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@ToString
public class ViaggioPayload {

    private String destinazione;
    private LocalDate data;
}
