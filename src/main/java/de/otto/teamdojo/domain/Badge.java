package de.otto.teamdojo.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Badge.
 */
@Entity
@Table(name = "badge")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Badge implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(min = 2, max = 20)
    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "available_until")
    private Instant availableUntil;

    @Min(value = 1)
    @Column(name = "available_amount")
    private Integer availableAmount;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "1")
    @Column(name = "required_score", nullable = false)
    private Double requiredScore;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "instant_multiplier", nullable = false)
    private Double instantMultiplier;

    @Min(value = 0)
    @Column(name = "completion_bonus")
    private Integer completionBonus;

    @OneToMany(mappedBy = "badge")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<BadgeSkill> skills = new HashSet<>();
    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "badge_dimensions",
               joinColumns = @JoinColumn(name = "badge_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "dimensions_id", referencedColumnName = "id"))
    private Set<Dimension> dimensions = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("badges")
    private Image image;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Badge name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Badge description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getAvailableUntil() {
        return availableUntil;
    }

    public Badge availableUntil(Instant availableUntil) {
        this.availableUntil = availableUntil;
        return this;
    }

    public void setAvailableUntil(Instant availableUntil) {
        this.availableUntil = availableUntil;
    }

    public Integer getAvailableAmount() {
        return availableAmount;
    }

    public Badge availableAmount(Integer availableAmount) {
        this.availableAmount = availableAmount;
        return this;
    }

    public void setAvailableAmount(Integer availableAmount) {
        this.availableAmount = availableAmount;
    }

    public Double getRequiredScore() {
        return requiredScore;
    }

    public Badge requiredScore(Double requiredScore) {
        this.requiredScore = requiredScore;
        return this;
    }

    public void setRequiredScore(Double requiredScore) {
        this.requiredScore = requiredScore;
    }

    public Double getInstantMultiplier() {
        return instantMultiplier;
    }

    public Badge instantMultiplier(Double instantMultiplier) {
        this.instantMultiplier = instantMultiplier;
        return this;
    }

    public void setInstantMultiplier(Double instantMultiplier) {
        this.instantMultiplier = instantMultiplier;
    }

    public Integer getCompletionBonus() {
        return completionBonus;
    }

    public Badge completionBonus(Integer completionBonus) {
        this.completionBonus = completionBonus;
        return this;
    }

    public void setCompletionBonus(Integer completionBonus) {
        this.completionBonus = completionBonus;
    }

    public Set<BadgeSkill> getSkills() {
        return skills;
    }

    public Badge skills(Set<BadgeSkill> badgeSkills) {
        this.skills = badgeSkills;
        return this;
    }

    public Badge addSkills(BadgeSkill badgeSkill) {
        this.skills.add(badgeSkill);
        badgeSkill.setBadge(this);
        return this;
    }

    public Badge removeSkills(BadgeSkill badgeSkill) {
        this.skills.remove(badgeSkill);
        badgeSkill.setBadge(null);
        return this;
    }

    public void setSkills(Set<BadgeSkill> badgeSkills) {
        this.skills = badgeSkills;
    }

    public Set<Dimension> getDimensions() {
        return dimensions;
    }

    public Badge dimensions(Set<Dimension> dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    public Badge addDimensions(Dimension dimension) {
        this.dimensions.add(dimension);
        dimension.getBadges().add(this);
        return this;
    }

    public Badge removeDimensions(Dimension dimension) {
        this.dimensions.remove(dimension);
        dimension.getBadges().remove(this);
        return this;
    }

    public void setDimensions(Set<Dimension> dimensions) {
        this.dimensions = dimensions;
    }

    public Image getImage() {
        return image;
    }

    public Badge image(Image image) {
        this.image = image;
        return this;
    }

    public void setImage(Image image) {
        this.image = image;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Badge badge = (Badge) o;
        if (badge.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), badge.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Badge{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", availableUntil='" + getAvailableUntil() + "'" +
            ", availableAmount=" + getAvailableAmount() +
            ", requiredScore=" + getRequiredScore() +
            ", instantMultiplier=" + getInstantMultiplier() +
            ", completionBonus=" + getCompletionBonus() +
            "}";
    }
}
