package org.cedacri.pingpong.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "players") // Specifică numele tablei, dacă diferă de numele clasei
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID generat automat de baza de date
    private Long id;

    private String name;
    private Integer rank;
    private Integer age;
    private String style;

    // Constructor complet
    public Player(Long id, String name, Integer rank, Integer age, String style) {
        this.id = id;
        this.name = name;
        this.rank = rank;
        this.age = age;
        this.style = style;
    }

    // Constructor implicit (necesar pentru JPA)
    public Player() {}

    // Getteri și setteri
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

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
