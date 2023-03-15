package study.datajpa.dto;

import lombok.Data;

@Data // 단순 DTO. 엔티티에는 웬만하면 쓰면 안 됨!
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }
}
