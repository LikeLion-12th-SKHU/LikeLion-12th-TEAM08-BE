package org.likelion.tm8.weight.domain;

import jakarta.persistence.*;
<<<<<<< HEAD
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.likelion.tm8.user.domain.User;
import org.likelion.tm8.weight.api.dto.request.WeightUpdateReqDto;

@Entity
@Getter
@NoArgsConstructor
=======
import org.likelion.tm8.user.domain.User;

@Entity
>>>>>>> 9ebebefa0296ea18f78e38b4d3f5a7c4aef7409e
public class Weight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weight_id")
    private Long weightId;

<<<<<<< HEAD
    // 몸무게
    @Column(name = "weight")
    private Long weight;

    // 컨디션
    @Column(name = "condition")
    private String condition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    private Weight(Long weightId, Long weight, String condition, User user) {
        this.weightId = weightId;
        this.weight = weight;
        this.condition = condition;
        this.user = user;
    }

    public void update(WeightUpdateReqDto weightUpdateReqDto) {
        this.weight = weightUpdateReqDto.weight();
        this.condition = weightUpdateReqDto.condition();
    }
=======
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
>>>>>>> 9ebebefa0296ea18f78e38b4d3f5a7c4aef7409e
}
