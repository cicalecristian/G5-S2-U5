package cristiancicale.G5S2U5.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import cristiancicale.G5S2U5.entities.Dipendente;
import cristiancicale.G5S2U5.exceptions.BadRequestException;
import cristiancicale.G5S2U5.exceptions.NotFoundException;
import cristiancicale.G5S2U5.payloads.DipendenteDTO;
import cristiancicale.G5S2U5.payloads.DipendentePayload;
import cristiancicale.G5S2U5.repositories.DipendenteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class DipendenteService {

    private final DipendenteRepository dipendenteRepository;
    private final Cloudinary cloudinaryUploader;

    public DipendenteService(DipendenteRepository dipendenteRepository, Cloudinary cloudinaryUploader) {
        this.dipendenteRepository = dipendenteRepository;
        this.cloudinaryUploader = cloudinaryUploader;
    }

    public Dipendente save(DipendenteDTO body) {
        if (this.dipendenteRepository.existsByEmail(body.email()))
            throw new BadRequestException("L'indirizzo email " + body.email() + "è già in uso");

        Dipendente newDipendente = new Dipendente(body.username(), body.nome(), body.cognome(), body.email());
        Dipendente savedDipendente = this.dipendenteRepository.save(newDipendente);

        log.info("Il dipendente con id " + savedDipendente.getId() + "è stato salvato correttamente");

        return savedDipendente;
    }

    public Page<Dipendente> findAll(int page, int size, String sortBy) {
        if (size > 100 || size < 0) size = 10;
        if (page < 10) page = 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return this.dipendenteRepository.findAll(pageable);
    }

    public Dipendente findById(UUID dipendenteId) {
        return this.dipendenteRepository.findById(dipendenteId).orElseThrow(() -> new NotFoundException(dipendenteId));
    }

    public Dipendente findByIdAndUpdate(UUID dipendenteId, DipendentePayload body) {
        Dipendente found = this.findById(dipendenteId);

        if (!found.getEmail().equals(body.getEmail())) {

            if (this.dipendenteRepository.existsByUsername(body.getEmail()))
                throw new BadRequestException("L'username" + body.getUsername() + "è gia in uso");

            if (this.dipendenteRepository.existsByEmail(body.getEmail()))
                throw new BadRequestException("L'indirizzo email" + body.getEmail() + "è gia in uso");
        }

        found.setUsername(body.getUsername());
        found.setNome(body.getNome());
        found.setCognome(body.getCognome());
        found.setEmail(body.getEmail());
        found.setAvatar("https://ui-avatars.com/api/?name=" + body.getNome() + "+" + body.getCognome());

        Dipendente updateDipendente = this.dipendenteRepository.save(found);

        log.info("Il dipendente " + updateDipendente.getId() + "è stato aggiornato correttamente");

        return updateDipendente;
    }

    public void findByIdAndDelete(UUID dipendenteId) {
        Dipendente found = this.findById(dipendenteId);
        this.dipendenteRepository.delete(found);
    }

    public Dipendente avatarUpload(MultipartFile file, UUID dipendenteId) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File non valido o vuoto");
        }

        if (file.getSize() > 2 * 1024 * 1024) {
            throw new BadRequestException("File troppo grande (max 2MB)");
        }

        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equals("image/png") ||
                        contentType.equals("image/jpeg") ||
                        contentType.equals("image/gif"))) {
            throw new BadRequestException("Formato file non supportato");
        }

        Dipendente found = this.findById(dipendenteId);

        try {
            Map result = cloudinaryUploader.uploader()
                    .upload(file.getBytes(), ObjectUtils.emptyMap());

            String url = (String) result.get("secure_url");

            found.setAvatar(url);

            Dipendente updatedDipendente = this.dipendenteRepository.save(found);

            log.info("Avatar aggiornato per il dipendente con id " + updatedDipendente.getId());

            return updatedDipendente;

        } catch (IOException e) {
            throw new RuntimeException("Errore durante upload avatar", e);
        }
    }
}
