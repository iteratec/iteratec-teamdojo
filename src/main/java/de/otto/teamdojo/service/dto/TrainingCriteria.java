package de.otto.teamdojo.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the Training entity. This class is used in TrainingResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /trainings?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class TrainingCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter description;

    private StringFilter contactPerson;

    private StringFilter link;

    private InstantFilter validUntil;

    private BooleanFilter isOfficial;

    private StringFilter suggestedBy;

    private LongFilter skillId;

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

    public StringFilter getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(StringFilter contactPerson) {
        this.contactPerson = contactPerson;
    }

    public StringFilter getLink() {
        return link;
    }

    public void setLink(StringFilter link) {
        this.link = link;
    }

    public InstantFilter getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(InstantFilter validUntil) {
        this.validUntil = validUntil;
    }

    public BooleanFilter getIsOfficial() {
        return isOfficial;
    }

    public void setIsOfficial(BooleanFilter isOfficial) {
        this.isOfficial = isOfficial;
    }

    public StringFilter getSuggestedBy() {
        return suggestedBy;
    }

    public void setSuggestedBy(StringFilter suggestedBy) {
        this.suggestedBy = suggestedBy;
    }

    public LongFilter getSkillId() {
        return skillId;
    }

    public void setSkillId(LongFilter skillId) {
        this.skillId = skillId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TrainingCriteria that = (TrainingCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(description, that.description) &&
            Objects.equals(contactPerson, that.contactPerson) &&
            Objects.equals(link, that.link) &&
            Objects.equals(validUntil, that.validUntil) &&
            Objects.equals(isOfficial, that.isOfficial) &&
            Objects.equals(suggestedBy, that.suggestedBy) &&
            Objects.equals(skillId, that.skillId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        title,
        description,
        contactPerson,
        link,
        validUntil,
        isOfficial,
        suggestedBy,
        skillId
        );
    }

    @Override
    public String toString() {
        return "TrainingCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (title != null ? "title=" + title + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (contactPerson != null ? "contactPerson=" + contactPerson + ", " : "") +
                (link != null ? "link=" + link + ", " : "") +
                (validUntil != null ? "validUntil=" + validUntil + ", " : "") +
                (isOfficial != null ? "isOfficial=" + isOfficial + ", " : "") +
                (suggestedBy != null ? "suggestedBy=" + suggestedBy + ", " : "") +
                (skillId != null ? "skillId=" + skillId + ", " : "") +
            "}";
    }

}
