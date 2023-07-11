package com.iwor.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.SortNatural;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.UniqueConstraint;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
@ToString(exclude = {"users"})
@Builder
@Entity
@Audited
public class Company implements BaseEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "company", cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.REFRESH
    })
    @SortNatural
    @MapKey(name = "username")
    private Map<String, User> users = new TreeMap<>();

    @Audited
    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "company_locale", uniqueConstraints = @UniqueConstraint(columnNames = {"lang", "company_id"}))
    @Column(name = "description")
    @MapKeyColumn(name = "lang", columnDefinition = "bpchar", length = 2)
    private Map<String, String> locales = new HashMap<>();

    @PreRemove
    private void nullification() {
        users.values().forEach(user -> user.setCompany(null));
    }

    public void addUser(User... users) {
        Arrays.stream(users).forEach(user -> {
                    this.users.put(user.getUsername(), user);
                    user.setCompany(this);
        });
    }
}
