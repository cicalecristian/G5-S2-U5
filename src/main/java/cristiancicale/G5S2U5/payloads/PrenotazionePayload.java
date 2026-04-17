package cristiancicale.G5S2U5.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
public class PrenotazionePayload {

    private String note;
    private UUID dipendenteId;
    private UUID viaggioId;
}
