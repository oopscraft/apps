package org.oopscraft.apps.core.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearch {

    private String id;

    private String name;

    private User.Type type;

    private User.Status status;

    private String email;

    private String mobile;

}
