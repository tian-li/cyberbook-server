package app.cyberbook.cyberbookserver.model;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "cyberbook_user")
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = {"id"})
public class User {
    @ElementCollection(fetch = FetchType.EAGER)
    List<Role> roles;
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    private String username;
    private String password;
    private String email;
    private Integer gender;
    private String birthday;
    private String theme;
    private Boolean registered;
    @ManyToMany(targetEntity = MessageThread.class)
    private List<MessageThread> messageThreads;

    //    @ElementCollection
//    private List<String> messageThreadIds;
    private String profilePhotoUrl;
    private String dateRegistered;

    public String getUsernameOrEmail() {
        return this.getEmail() != null ? this.getEmail() : this.getUsername();
    }
}