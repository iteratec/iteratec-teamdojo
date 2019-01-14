package de.otto.teamdojo.service;

import de.otto.teamdojo.service.dto.ImageDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Image.
 */
public interface ImageService {

    /**
     * Save a image.
     *
     * @param imageDTO the entity to save
     * @return the persisted entity
     */
    ImageDTO save(ImageDTO imageDTO);

    /**
     * Get all the images.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<ImageDTO> findAll(Pageable pageable);


    /**
     * Get the "id" image.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<ImageDTO> findOne(Long id);

    /**
     * Delete the "id" image.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
