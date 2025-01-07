package com.busanit501.bootproject.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "MatchingRoom")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
// Board 해당하는 엔티티이고,
public class MatchingRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostId")
    private User host;

    @Column(nullable = false, length = 255)
    private String title;

    private String description;

    @Column(nullable = false, columnDefinition = "LONG DEFAULT 10")
    private Long maxParticipants;

    @Column(nullable = false, columnDefinition = "LONG DEFAULT 1")
    private Long currentParticipants;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('Open', 'Closed') DEFAULT 'Open'")
    private RoomStatus status;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @OneToMany(mappedBy = "chatRoom",
            cascade = CascadeType.ALL // 부모 테이블의 변경을 , 자식 테이블에서도 같이 적용됨.
            ,fetch = FetchType.LAZY,
            orphanRemoval = true) // 고아 객체 자동 삭제 설정
    @BatchSize(size = 20)
    @Builder.Default
    private Set<Message> messageSet = new HashSet<>();

    @OneToMany(mappedBy = "chatRoom",
            cascade = CascadeType.ALL // 부모 테이블의 변경을 , 자식 테이블에서도 같이 적용됨.
            ,fetch = FetchType.LAZY,
            orphanRemoval = true) // 고아 객체 자동 삭제 설정
    @BatchSize(size = 20)
    @Builder.Default
    private Set<RoomParticipants> participantSet = new HashSet<>();




    public void MatchingRoomUpdate(String title,
                                   String description,
                                   long maxParticipants,
                                   RoomStatus status) {
        this.title = title;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.status = status;
    }

    public void exitRoom(long currentParticipants){
        this.currentParticipants = currentParticipants - 1;
    }
    public void inviteRoom(long currentParticipants){
        this.currentParticipants = currentParticipants + 1;
    }
}

