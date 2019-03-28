package de.otto.teamdojo.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the Skill entity. This class is used in SkillResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /skills?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class SkillCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter description;

    private StringFilter implementation;

    private StringFilter validation;

    private IntegerFilter expiryPeriod;

    private StringFilter contact;

    private IntegerFilter score;

    private DoubleFilter rateScore;

    private IntegerFilter rateCount;

    private LongFilter teamsId;

    private LongFilter badgesId;

    private LongFilter levelsId;

    private LongFilter trainingsId;

    public SkillCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTitle() {
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getImplementation() {
        return implementation;
    }

    public void setImplementation(StringFilter implementation) {
        this.implementation = implementation;
    }

    public StringFilter getValidation() {
        return validation;
    }

    public void setValidation(StringFilter validation) {
        this.validation = validation;
    }

    public IntegerFilter getExpiryPeriod() {
        return expiryPeriod;
    }

    public void setExpiryPeriod(IntegerFilter expiryPeriod) {
        this.expiryPeriod = expiryPeriod;
    }

    public StringFilter getContact() {
        return contact;
    }

    public void setContact(StringFilter contact) {
        this.contact = contact;
    }

    public IntegerFilter getScore() {
        return score;
    }

    public void setScore(IntegerFilter score) {
        this.score = score;
    }

    public DoubleFilter getRateScore() {
        return rateScore;
    }

    public void setRateScore(DoubleFilter rateScore) {
        this.rateScore = rateScore;
    }

    public IntegerFilter getRateCount() {
        return rateCount;
    }

    public void setRateCount(IntegerFilter rateCount) {
        this.rateCount = rateCount;
    }

    public LongFilter getTeamsId() {
        return teamsId;
    }

    public void setTeamsId(LongFilter teamsId) {
        this.teamsId = teamsId;
    }

    public LongFilter getBadgesId() {
        return badgesId;
    }

    public void setBadgesId(LongFilter badgesId) {
        this.badgesId = badgesId;
    }

    public LongFilter getLevelsId() {
        return levelsId;
    }

    public void setLevelsId(LongFilter levelsId) {
        this.levelsId = levelsId;
    }

    public LongFilter getTrainingsId() {
        return trainingsId;
    }

    public void setTrainingsId(LongFilter trainingsId) {
        this.trainingsId = trainingsId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SkillCriteria that = (SkillCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(description, that.description) &&
            Objects.equals(implementation, that.implementation) &&
            Objects.equals(validation, that.validation) &&
            Objects.equals(expiryPeriod, that.expiryPeriod) &&
            Objects.equals(contact, that.contact) &&
            Objects.equals(score, that.score) &&
            Objects.equals(rateScore, that.rateScore) &&
            Objects.equals(rateCount, that.rateCount) &&
            Objects.equals(teamsId, that.teamsId) &&
            Objects.equals(badgesId, that.badgesId) &&
            Objects.equals(levelsId, that.levelsId) &&
            Objects.equals(trainingsId, that.trainingsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        title,
        description,
        implementation,
        validation,
        expiryPeriod,
        contact,
        score,
        rateScore,
        rateCount,
        teamsId,
        badgesId,
        levelsId,
        trainingsId
        );
    }

    @Override
    public String toString() {
        return "SkillCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (implementation != null ? "implementation=" + implementation + ", " : "") +
            (validation != null ? "validation=" + validation + ", " : "") +
            (expiryPeriod != null ? "expiryPeriod=" + expiryPeriod + ", " : "") +
            (contact != null ? "contact=" + contact + ", " : "") +
            (score != null ? "score=" + score + ", " : "") +
            (rateScore != null ? "rateScore=" + rateScore + ", " : "") +
            (rateCount != null ? "rateCount=" + rateCount + ", " : "") +
            (teamsId != null ? "teamsId=" + teamsId + ", " : "") +
            (badgesId != null ? "badgesId=" + badgesId + ", " : "") +
            (levelsId != null ? "levelsId=" + levelsId + ", " : "") +
            (trainingsId != null ? "trainingsId=" + trainingsId + ", " : "") +
            "}";
    }

}
