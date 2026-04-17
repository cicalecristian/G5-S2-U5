package cristiancicale.G5S2U5.services;

import cristiancicale.G5S2U5.entities.Dipendente;
import cristiancicale.G5S2U5.entities.Prenotazione;
import cristiancicale.G5S2U5.entities.Viaggio;
import cristiancicale.G5S2U5.exceptions.ValidationException;
import cristiancicale.G5S2U5.payloads.PrenotazioneDTO;
import cristiancicale.G5S2U5.repositories.PrenotazioneRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class PrenotazioneService {

    private final PrenotazioneRepository prenotazioneRepository;
    private final DipendenteService dipendenteService;
    private final ViaggioService viaggioService;


    public PrenotazioneService(PrenotazioneRepository prenotazioneRepository, DipendenteService dipendenteService, ViaggioService viaggioService) {
        this.prenotazioneRepository = prenotazioneRepository;
        this.dipendenteService = dipendenteService;
        this.viaggioService = viaggioService;
    }

    public Prenotazione save(PrenotazioneDTO body) {

        Dipendente dipendente = dipendenteService.findById(body.dipendenteId());

        Viaggio viaggio = viaggioService.findById(body.viaggioId());

        LocalDate dataViaggio = viaggio.getData();

        boolean exists = prenotazioneRepository
                .existsByDipendenteIdAndDataViaggio(dipendente.getId(), dataViaggio);

        if (exists) {
            throw new ValidationException(
                    List.of("Hai già una prenotazione per questa data")
            );
        }

        Prenotazione newPrenotazione = new Prenotazione(dipendente, viaggio, body.note());
        Prenotazione savedPrenotazione = this.prenotazioneRepository.save(newPrenotazione);

        log.info("La prenotazione con id " + savedPrenotazione.getId() + "è stata salvata correttamente");

        return savedPrenotazione;
    }
}
