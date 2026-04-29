package com.example.embabel.repository;

import com.example.embabel.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Slf4j
public class MemberRepositoryImpl implements MemberRepository {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String DATA_FILE_PATH = "data/members.json";

    private final Map<Long, Member> store = new ConcurrentHashMap<>();

    private final AtomicLong sequence = new AtomicLong(0L);

    public MemberRepositoryImpl() {
        loadDataFromFile();
    }

    //파일에 있던 회원 목록을 메모리로 불러오기
    private void loadDataFromFile() {
        File file =  new File(DATA_FILE_PATH);
        if (!file.exists()) {
            List<Member> members = objectMapper.readValue(file, new TypeReference<List<Member>>() {

            });
            for (Member member : members) {
                store.put(member.getId(), member);
                if (member.getId() > sequence.get()) {
                    sequence.set(member.getId() + 1);
                }
            }
            log.info("회원 데이터 로드 완료: {}명", members.size());
        }
        else {
            File directory = new File("data");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            log.info("기존 데이터 파일이 없어 새로 시작합니다.");
        }
    }

    private void saveDataToFile() {
        List<Member> members = new ArrayList<>(store.values());
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(DATA_FILE_PATH), members);
    }

    @Override
    public void save(Member member) {
        if (member.getId() == null) {
            long newId = sequence.incrementAndGet();
            member.setId(newId);
            store.put(newId, member);
        }
        else {
            store.put(member.getId(), member);
        }
        saveDataToFile();
    }
}
