package pers.cherish.userservice.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LongWrapper {

    private Long id;

    public LongWrapper(Long id) {
        this.id = id;
    }

    public static LongWrapper of(Long id) {
        return new LongWrapper(id);
    }

}
