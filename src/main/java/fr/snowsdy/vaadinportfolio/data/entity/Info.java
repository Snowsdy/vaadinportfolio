package fr.snowsdy.vaadinportfolio.data.entity;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
public class Info extends AbstractEntity {

    private String title;
    @Lob
    private String imagePath;
    private LocalDate addedAt;
    private String description;
    private String githubLink;
    private String language;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public LocalDate getAddedAt() {
        return addedAt;
    }
    public void setAddedAt(LocalDate addedAt) {
        this.addedAt = addedAt;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getGithubLink() {
        return githubLink;
    }
    public void setGithubLink(String githubLink) {
        this.githubLink = githubLink;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }

}
