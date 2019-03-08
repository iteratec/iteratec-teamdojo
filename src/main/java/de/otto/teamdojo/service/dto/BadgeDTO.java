package de.otto.teamdojo.service.dto;
import java.time.Instant;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A DTO for the Badge entity.
 */
public class BadgeDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 2, max = 20)
    private String name;

    private String description;

    private Instant availableUntil;

    @Min(value = 1)
    private Integer availableAmount;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "1")
    private Double requiredScore;

    @NotNull
    @DecimalMin(value = "0")
    private Double instantMultiplier;

    @Min(value = 0)
    private Integer completionBonus;


    private Set<DimensionDTO> dimensions = new HashSet<>();

    private Long imageId;

    private String imageHash;

    private String imageName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getAvailableUntil() {
        return availableUntil;
    }

    public void setAvailableUntil(Instant availableUntil) {
        this.availableUntil = availableUntil;
    }

    public Integer getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(Integer availableAmount) {
        this.availableAmount = availableAmount;
    }

    public Double getRequiredScore() {
        return requiredScore;
    }

    public void setRequiredScore(Double requiredScore) {
        this.requiredScore = requiredScore;
    }

    public Double getInstantMultiplier() {
        return instantMultiplier;
    }

    public void setInstantMultiplier(Double instantMultiplier) {
        this.instantMultiplier = instantMultiplier;
    }

    public Integer getCompletionBonus() {
        return completionBonus;
    }

    public void setCompletionBonus(Integer completionBonus) {
        this.completionBonus = completionBonus;
    }

    public Set<DimensionDTO> getDimensions() {
        return dimensions;
    }

    public void setDimensions(Set<DimensionDTO> dimensions) {
        this.dimensions = dimensions;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public String getImageHash() {
        return imageHash;
    }

    public void setImageHash(String imageHash) {
        this.imageHash = imageHash;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BadgeDTO badgeDTO = (BadgeDTO) o;
        if (badgeDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), badgeDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "BadgeDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", availableUntil='" + getAvailableUntil() + "'" +
            ", availableAmount=" + getAvailableAmount() +
            ", requiredScore=" + getRequiredScore() +
            ", instantMultiplier=" + getInstantMultiplier() +
            ", completionBonus=" + getCompletionBonus() +
            ", image=" + getImageId() +
            ", imageName='" + getImageName() + "'" +
            ", imageHash='" + getImageHash() + "'" +
            "}";
    }
}
