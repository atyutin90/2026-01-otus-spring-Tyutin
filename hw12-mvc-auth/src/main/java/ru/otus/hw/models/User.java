package ru.otus.hw.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;
import static ru.otus.hw.models.User.USER_GRAPH;

@Getter
@Setter
@EqualsAndHashCode(exclude = {"roles"})
@ToString(exclude = {"roles"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
@NamedEntityGraph(name = USER_GRAPH, attributeNodes = {@NamedAttributeNode("roles")})
public class User {

    public static final String USER_GRAPH = "user-graph";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;
}

