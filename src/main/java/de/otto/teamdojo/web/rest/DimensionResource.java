package de.otto.teamdojo.web.rest;
import de.otto.teamdojo.service.DimensionService;
import de.otto.teamdojo.web.rest.errors.BadRequestAlertException;
import de.otto.teamdojo.web.rest.util.HeaderUtil;
import de.otto.teamdojo.web.rest.util.PaginationUtil;
import de.otto.teamdojo.service.dto.DimensionDTO;
import de.otto.teamdojo.service.dto.DimensionCriteria;
import de.otto.teamdojo.service.DimensionQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Dimension.
 */
@RestController
@RequestMapping("/api")
public class DimensionResource {

    private final Logger log = LoggerFactory.getLogger(DimensionResource.class);

    private static final String ENTITY_NAME = "dimension";

    private final DimensionService dimensionService;

    private final DimensionQueryService dimensionQueryService;

    public DimensionResource(DimensionService dimensionService, DimensionQueryService dimensionQueryService) {
        this.dimensionService = dimensionService;
        this.dimensionQueryService = dimensionQueryService;
    }

    /**
     * POST  /dimensions : Create a new dimension.
     *
     * @param dimensionDTO the dimensionDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new dimensionDTO, or with status 400 (Bad Request) if the dimension has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/dimensions")
    public ResponseEntity<DimensionDTO> createDimension(@Valid @RequestBody DimensionDTO dimensionDTO) throws URISyntaxException {
        log.debug("REST request to save Dimension : {}", dimensionDTO);
        if (dimensionDTO.getId() != null) {
            throw new BadRequestAlertException("A new dimension cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DimensionDTO result = dimensionService.save(dimensionDTO);
        return ResponseEntity.created(new URI("/api/dimensions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /dimensions : Updates an existing dimension.
     *
     * @param dimensionDTO the dimensionDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated dimensionDTO,
     * or with status 400 (Bad Request) if the dimensionDTO is not valid,
     * or with status 500 (Internal Server Error) if the dimensionDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/dimensions")
    public ResponseEntity<DimensionDTO> updateDimension(@Valid @RequestBody DimensionDTO dimensionDTO) throws URISyntaxException {
        log.debug("REST request to update Dimension : {}", dimensionDTO);
        if (dimensionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        DimensionDTO result = dimensionService.save(dimensionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, dimensionDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /dimensions : get all the dimensions.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of dimensions in body
     */
    @GetMapping("/dimensions")
    public ResponseEntity<List<DimensionDTO>> getAllDimensions(DimensionCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Dimensions by criteria: {}", criteria);
        Page<DimensionDTO> page = dimensionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/dimensions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
    * GET  /dimensions/count : count all the dimensions.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/dimensions/count")
    public ResponseEntity<Long> countDimensions(DimensionCriteria criteria) {
        log.debug("REST request to count Dimensions by criteria: {}", criteria);
        return ResponseEntity.ok().body(dimensionQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /dimensions/:id : get the "id" dimension.
     *
     * @param id the id of the dimensionDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the dimensionDTO, or with status 404 (Not Found)
     */
    @GetMapping("/dimensions/{id}")
    public ResponseEntity<DimensionDTO> getDimension(@PathVariable Long id) {
        log.debug("REST request to get Dimension : {}", id);
        Optional<DimensionDTO> dimensionDTO = dimensionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dimensionDTO);
    }

    /**
     * DELETE  /dimensions/:id : delete the "id" dimension.
     *
     * @param id the id of the dimensionDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/dimensions/{id}")
    public ResponseEntity<Void> deleteDimension(@PathVariable Long id) {
        log.debug("REST request to delete Dimension : {}", id);
        dimensionService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
