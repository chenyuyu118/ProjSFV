package pers.cherish.userservice.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Data
@NoArgsConstructor
public class Relation {
    public byte[] fanRelation;
    public byte[] friendRelation;

    public boolean isFriend(long id, long otherId) {
//        System.out.println("friend relations:" + Arrays.toString(friendRelation));
        if (id > otherId)
            return (friendRelation[0] & 0b01) != 0;
        else
            return (friendRelation[0] & 0b10) != 0;
    }

    public boolean isFollow(long id, long otherId) {
//        System.out.println("Fan relations:" + Arrays.toString(fanRelation));
        if (id > otherId)
            return (fanRelation[0] & 0b10) != 0;
        else
            return (fanRelation[0] & 0b01) != 0;
    }
}
