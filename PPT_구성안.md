# 🍃 Spring Boot 입문 발표 PPT 구성안

> **발표 대상:** Spring Boot를 처음 접하는 초보자  
> **발표 시간:** 약 30-40분  
> **예시 프로젝트:** 회원 관리 시스템 (CRUD + 로그인)

---

## 📋 슬라이드 구성 (총 16장)

**구성 원칙:**
1. **개념 먼저** (슬라이드 3-8): Spring Boot의 핵심 개념, 어노테이션, 도구 설명
2. **실습 나중** (슬라이드 9-15): 실제 프로젝트 코드를 보며 실습

---

### **슬라이드 1: 제목 슬라이드**

**제목:** 🍃 Spring Boot 입문: 백엔드 개발의 시작

**부제목:** 
- 계층형 아키텍처부터 실전 프로젝트까지
- 초보자를 위한 A to Z 가이드

**하단:**
- 발표자 이름
- 날짜

**디자인 팁:**
- Spring Boot 로고 사용
- 깔끔한 그라데이션 배경

---

### **슬라이드 2: 오늘 배울 내용 (목차)**

**제목:** 오늘 배울 내용

**내용:**

**📚 개념 학습 (Part 1)**
1. ✅ Spring vs Spring Boot: 왜 Boot인가?
2. ✅ Spring Boot의 3대 핵심 무기
3. ✅ 계층형 아키텍처 (식당 비유)
4. ✅ 필수 어노테이션 완벽 정리
5. ✅ Lombok으로 코드 간소화하기
6. ✅ 비밀번호 해싱 (BCrypt) 개념

**💻 실습 프로젝트 (Part 2)**
7. ✅ 실제 프로젝트 구조 살펴보기
8. ✅ Controller 계층 코드 실습
9. ✅ Service 계층 코드 실습
10. ✅ Repository 계층 코드 실습
11. ✅ API 동작 흐름 따라가기

**디자인 팁:**
- 체크박스 아이콘 사용
- 번호별로 애니메이션 효과

---

### **슬라이드 3: Spring vs Spring Boot**

**제목:** Spring vs Spring Boot: 왜 Boot인가?

**비유:** 🛒 마트 장보기 vs 🍱 밀키트

**표 구성:**

| 구분 | Spring (Legacy) | Spring Boot |
|:---|:---|:---|
| **비유** | 🛒 **마트 장보기**<br>재료 하나하나 직접 구매 | 🍱 **밀키트**<br>손질된 재료 + 레시피 포함 |
| **설정** | 복잡한 XML 설정 필요 | `application.properties`로 간편 설정 |
| **서버** | Tomcat 별도 설치 필요 | **내장 Tomcat** (실행만 하면 됨) |
| **의존성** | 라이브러리 버전 호환성 직접 체크 | `Starter`로 버전 자동 관리 |

**핵심 메시지:**
> "Spring Boot는 복잡한 설정을 자동화하여 **비즈니스 로직에만 집중**할 수 있게 해줍니다!"

**설명 포인트:**
- 개념 설명만 (코드는 나중에)
- 비유를 통한 이해
- 핵심 차이점 강조

---

### **슬라이드 4: Spring Boot의 3대 핵심 무기**

**제목:** Spring Boot의 3대 핵심 무기 ⚔️

**내용:**

#### 1️⃣ Dependency Management (의존성 관리)
- `spring-boot-starter-web` 하나만 적으면?
- → 관련된 수십 개의 라이브러리 자동 설치!
- (JSON, Tomcat, Hibernate 등)

#### 2️⃣ Auto Configuration (자동 설정)
- `@SpringBootApplication` 하나로 끝!
- → 자주 사용하는 설정을 스프링이 알아서 세팅

#### 3️⃣ Embedded WAS (내장 웹 서버)
- 별도의 웹 서버 설치? ❌
- `main()` 메서드 실행만으로 서버 구동! ✅

**설명 포인트:**
- 개념 설명만 (코드는 나중에)
- 각 무기의 역할과 장점 강조
- 왜 편리한지 설명

**비유:**
> "레고 블록 세트를 사면 설명서와 필요한 부품이 모두 들어있는 것처럼!"

---

### **슬라이드 5: 계층형 아키텍처 소개**

**제목:** 계층형 아키텍처: 식당에 비유하면 이해가 쉬워요! 🍽️

**다이어그램:**
```
[고객] 
   ↓ 주문
[웨이터] ← Controller
   ↓ 주문서 전달
[셰프] ← Service
   ↓ 재료 요청
[창고지기] ← Repository
   ↓ 재료 꺼내기
[냉장고] ← Database
```

**각 계층의 역할:**

| 계층 | 역할 | 비유 | 책임 |
|:---|:---|:---|:---|
| **Controller** | 요청/응답 처리 | 🤵 웨이터 | 주문 접수, 서빙 |
| **Service** | 비즈니스 로직 | 👨‍🍳 셰프 | 요리 진행, 트랜잭션 관리 |
| **Repository** | 데이터 접근 | 📦 창고지기 | 재료 꺼내기, 정리하기 |

**핵심 원칙:**
> ⚠️ **Controller는 절대 비즈니스 로직(요리)을 직접 하지 않습니다!**

**시각적 효과:**
- 각 계층을 다른 색상으로 구분
- 화살표로 데이터 흐름 표시

---

### **슬라이드 6: 실제 프로젝트 구조 보기**

**제목:** 실제 프로젝트 구조를 살펴봅시다! 📁

**프로젝트 트리 구조:**
```
src/main/java/com/example/demo/
├── controller/
│   └── MemberController.java    ← 웨이터 (요청 처리)
├── service/
│   └── MemberService.java       ← 셰프 (비즈니스 로직)
├── repository/
│   ├── MemberRepository.java    ← 창고 인터페이스
│   └── MemberRepositoryImpl.java ← 창고지기 (데이터 접근)
├── domain/
│   └── Member.java              ← 회원 엔티티 (데이터 모델)
└── DTO/
    ├── CreateMemberRequest.java  ← 요청용 DTO
    └── MemberResponse.java      ← 응답용 DTO
```

**설명:**
- 각 폴더가 계층을 나타냄
- `domain`: 실제 데이터 구조 (Member)
- `DTO`: 계층 간 데이터 전달용 객체

**설명 포인트:**
- 개념 설명만 (코드는 나중에)
- 각 계층의 역할과 책임 명확히
- 계층 간 관계 설명

---

### **슬라이드 6: 필수 어노테이션 정리 (개념)**

**제목:** 필수 어노테이션: 스프링의 마법 주문 ✨

**설명:**
> "어노테이션은 스프링에게 '이 클래스/메서드가 무엇인지' 알려주는 명찰입니다!"

**표 구성:**

| 어노테이션 | 설명 | 위치 | 비유 |
|:---|:---|:---|:---|
| `@RestController` | JSON 반환 컨트롤러 | Controller 클래스 | "이 클래스는 웨이터야!" |
| `@Service` | 비즈니스 로직 서비스 | Service 클래스 | "이 클래스는 셰프야!" |
| `@Repository` | 데이터 저장소 | Repository 클래스 | "이 클래스는 창고지기야!" |
| `@RequiredArgsConstructor` | 생성자 자동 생성 | 모든 계층 | "의존성을 자동으로 주입해줘!" |
| `@GetMapping` | GET 요청 처리 | Controller 메서드 | "조회 요청 받기" |
| `@PostMapping` | POST 요청 처리 | Controller 메서드 | "등록 요청 받기" |
| `@PutMapping` | PUT 요청 처리 | Controller 메서드 | "수정 요청 받기" |
| `@DeleteMapping` | DELETE 요청 처리 | Controller 메서드 | "삭제 요청 받기" |
| `@RequestBody` | JSON → 객체 변환 | Controller 파라미터 | "JSON을 Java 객체로 변환" |
| `@PathVariable` | URL 경로 변수 | Controller 파라미터 | "URL에서 값 추출" |

**어노테이션 레벨 설명:**

**클래스 레벨 어노테이션:**
- 클래스 위에 붙여서 "이 클래스의 역할"을 지정
- `@RestController`, `@Service`, `@Repository`

**메서드 레벨 어노테이션:**
- 메서드 위에 붙여서 "어떤 HTTP 요청을 처리할지" 지정
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`

**파라미터 레벨 어노테이션:**
- 파라미터 앞에 붙여서 "어디서 데이터를 가져올지" 지정
- `@RequestBody`: 요청 Body에서 가져오기
- `@PathVariable`: URL 경로에서 가져오기

**설명 포인트:**
- 개념 설명만 (코드는 나중에)
- 각 어노테이션의 역할과 사용 위치
- 왜 필요한지 설명

---

### **슬라이드 7: Lombok 개념 설명**

**제목:** Lombok: 반복 코드를 줄이는 마법 🪄

**문제 상황:**
> "Java에서 클래스를 만들 때마다 Getter, Setter, 생성자를 일일이 작성해야 합니다.  
> 4개 필드면 20줄 이상의 반복 코드가 생깁니다!"

**Before & After 비교:**

#### ❌ Lombok 없이 (Before)
```java
public class Member {
    private Long id;
    private String username;
    
    // 기본 생성자
    public Member() {
    }
    
    // Getter 메서드들
    public Long getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }
    
    // Setter 메서드들
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    // ... (총 20줄 이상)
}
```

#### ✅ Lombok 사용 (After)
```java
@Getter
@Setter
@NoArgsConstructor  // ← 기본 생성자 자동 생성
public class Member {
    private Long id;
    private String username;
}
// 단 7줄로 끝!
```

**주요 어노테이션:**

| 어노테이션 | 기능 | 설명 |
|:---|:---|:---|
| `@Getter` | getter 메서드 자동 생성 | `getId()`, `getUsername()` 등 |
| `@Setter` | setter 메서드 자동 생성 | `setId()`, `setUsername()` 등 |
| `@NoArgsConstructor` | 기본 생성자 생성 | `Member()` 생성자 |
| `@RequiredArgsConstructor` | final 필드 생성자 | DI용 생성자 (의존성 주입) |
| `@AllArgsConstructor` | 모든 필드 생성자 | 모든 필드를 받는 생성자 |
| `@Data` | Getter + Setter + 기타 | DTO에 자주 사용 (편리함) |

**핵심 메시지:**
> "Lombok은 지루한 반복 코드를 어노테이션 하나로 해결해줍니다!  
> 코드가 간결해지고 가독성이 향상됩니다."

**설명 포인트:**
- 개념 설명만 (코드는 나중에)
- 왜 필요한지, 어떻게 도움이 되는지
- Before/After 비교로 효과 강조

---

### **슬라이드 8: BCrypt 비밀번호 해싱 개념**

**제목:** 비밀번호 해싱: 왜 필요한가? 🔐

**문제점:**
```
❌ 평문 저장 (위험!)
{
  "id": 1,
  "username": "admin",
  "password": "1234",  // ← 그대로 저장
  "name": "관리자"
}

만약 해커가 데이터베이스를 탈취하면?
→ 모든 사용자의 비밀번호가 그대로 노출됨!
```

**해결책: BCrypt 해싱**
```
✅ 해싱된 비밀번호 저장 (안전!)
{
  "id": 1,
  "username": "admin",
  "password": "$2a$10$N9qo8uLOickgx2ZMRZoMye...",  // ← 해싱됨
  "name": "관리자"
}

해커가 데이터베이스를 탈취해도?
→ 해시값만 보이므로 원래 비밀번호를 알 수 없음!
```

**BCrypt의 특징:**

1. ✅ **단방향 해싱**
   - 원래 비밀번호로 복원 불가능
   - 해시값에서 원문을 추출할 수 없음

2. ✅ **Salt 자동 생성**
   - 같은 비밀번호도 매번 다른 해시값 생성
   - "1234"를 두 번 저장해도 해시값이 다름

3. ✅ **느린 해싱**
   - 의도적으로 느리게 설계됨
   - 무차별 대입 공격(Brute Force) 방지

**비유:**
> "비밀번호를 해싱하는 것은 요리를 만드는 것과 같습니다.  
> 원재료(평문 비밀번호)를 요리(해싱)하면 완성된 요리(해시값)가 나오지만,  
> 요리에서 원재료를 다시 추출할 수는 없습니다!"

**동작 원리:**

**회원가입 시:**
```
사용자 입력: "1234"
         ↓
BCrypt 해싱
         ↓
해시값: "$2a$10$N9qo8uLOickgx2ZMRZoMye..."
         ↓
데이터베이스에 저장
```

**로그인 시:**
```
사용자 입력: "1234"
         ↓
데이터베이스에서 해시값 가져오기: "$2a$10$..."
         ↓
BCrypt 검증 (입력값과 해시값 비교)
         ↓
일치하면 로그인 성공!
```

**설명 포인트:**
- 개념 설명만 (코드는 나중에)
- 왜 필요한지, 어떻게 동작하는지
- 보안의 중요성 강조

---

### **슬라이드 9: 전환 슬라이드 - 이제 실습으로!**

**제목:** 이제 실제 프로젝트로! 💻

**메시지:**
> "지금까지 배운 개념들을 실제 코드에서 확인해봅시다!  
> 회원 관리 시스템 프로젝트를 통해 각 계층이 어떻게 동작하는지 살펴보겠습니다."

**프로젝트 소개:**
- **이름**: 회원 관리 시스템
- **기능**: 회원가입, 조회, 수정, 삭제, 로그인
- **구조**: Controller-Service-Repository 계층형 아키텍처

**다음 슬라이드에서:**
1. 프로젝트 구조 살펴보기
2. 각 계층의 코드 분석
3. API 동작 흐름 따라가기

**시각적 효과:**
- 화면 전환 애니메이션
- "개념 → 실습" 전환 강조

---

### **슬라이드 10: 실제 프로젝트 구조 보기**

**제목:** 실제 프로젝트 구조를 살펴봅시다! 📁

**프로젝트 트리 구조:**
```
src/main/java/com/example/demo/
├── controller/
│   └── MemberController.java    ← 웨이터 (요청 처리)
├── service/
│   └── MemberService.java       ← 셰프 (비즈니스 로직)
├── repository/
│   ├── MemberRepository.java    ← 창고 인터페이스
│   └── MemberRepositoryImpl.java ← 창고지기 (데이터 접근)
├── domain/
│   └── Member.java              ← 회원 엔티티 (데이터 모델)
└── DTO/
    ├── CreateMemberRequest.java  ← 요청용 DTO
    └── MemberResponse.java      ← 응답용 DTO
```

**설명:**
- 각 폴더가 계층을 나타냄
- `domain`: 실제 데이터 구조 (Member)
- `DTO`: 계층 간 데이터 전달용 객체

**코드 스크린샷 위치:**
- IDE의 프로젝트 트리 뷰
- 각 폴더를 클릭해서 보여주기

**실습 포인트:**
- "이제 이 구조를 코드로 확인해봅시다!"

---

### **슬라이드 11: Controller 계층 코드 실습 (웨이터)**

**제목:** Controller 계층 코드 실습: 웨이터의 역할 🤵

**비유 상기:**
> "웨이터는 고객의 주문을 받고, 주방에 전달하고, 완성된 요리를 서빙합니다."

**이제 실제 코드를 봅시다!**

**코드 스크린샷:**
```java
@RestController                    // ← 이 클래스는 REST API 컨트롤러!
@RequiredArgsConstructor          // ← 의존성 자동 주입
@RequestMapping("/api")          // ← 모든 URL은 /api로 시작
public class MemberController {
    
    private final MemberService memberService; // ← 셰프(Service)에게 의존
    
    @PostMapping("/members")      // ← POST 요청 처리
    public Result<Long> saveMember(@RequestBody CreateMemberRequest request) {
        // 1. DTO를 도메인 객체로 변환
        Member member = new Member();
        member.setUsername(request.getUsername());
        member.setPassword(request.getPassword());
        member.setName(request.getName());
        
        // 2. 셰프(Service)에게 요리 요청
        Long id = memberService.join(member);
        
        // 3. 결과를 고객에게 전달
        return new Result<>(id);
    }
}
```

**코드 설명 포인트 (발표 시):**

1. **클래스 레벨 어노테이션**
   ```java
   @RestController                    // ← 이전에 배운 개념! JSON 반환 컨트롤러
   @RequiredArgsConstructor          // ← 이전에 배운 개념! 의존성 자동 주입
   @RequestMapping("/api")           // ← 모든 URL은 /api로 시작
   ```

2. **의존성 주입**
   ```java
   private final MemberService memberService; 
   // ← 스프링이 자동으로 MemberService를 주입해줌
   ```

3. **HTTP 요청 매핑**
   ```java
   @PostMapping("/members")  // ← POST /api/members 요청 처리
   public Result<Long> saveMember(@RequestBody CreateMemberRequest request) {
       // @RequestBody: 이전에 배운 개념! JSON → Java 객체 자동 변환
   ```

4. **Controller의 역할**
   - DTO를 도메인 객체로 변환
   - Service 계층에 작업 요청
   - 결과를 JSON으로 반환
   - **비즈니스 로직은 없음!** (Service에 위임)

**실습 포인트:**
- "이 코드에서 배운 개념들이 어떻게 사용되는지 확인해봅시다!"
- "어노테이션이 실제로 어떻게 동작하는지 보세요!"

**코드 스크린샷:**
- `MemberController.java` 파일 전체
- 각 메서드별로 확대 설명 (회원가입, 조회, 수정, 삭제, 로그인)

---

### **슬라이드 12: Service 계층 코드 실습 (셰프)**

**제목:** Service 계층 코드 실습: 셰프의 역할 👨‍🍳

**비유 상기:**
> "셰프는 주문서를 받고, 요리를 진행하고, 재료가 필요하면 창고지기에게 요청합니다."

**이제 실제 코드를 봅시다!**

**코드 스크린샷:**
```java
@Service                          // ← 이 클래스는 서비스 계층!
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository; // ← 창고지기(Repository)에게 의존
    
    // 회원가입 비즈니스 로직
    public Long join(Member member) {
        // 1. 비밀번호를 BCrypt로 해싱 (보안 처리)
        String hashedPassword = BCrypt.hashpw(
            member.getPassword(), 
            BCrypt.gensalt()
        );
        member.setPassword(hashedPassword);
        
        // 2. 창고지기(Repository)에게 저장 요청
        memberRepository.save(member);
        
        return member.getId();
    }
    
    // 로그인 비즈니스 로직
    public Member login(String username, String password) {
        Member member = memberRepository.findByUsername(username);
        
        // BCrypt로 해싱된 비밀번호 검증
        if (member != null && 
            BCrypt.checkpw(password, member.getPassword())) {
            return member;
        }
        return null;
    }
}
```

**코드 설명 포인트 (발표 시):**

1. **클래스 레벨 어노테이션**
   ```java
   @Service  // ← 이전에 배운 개념! 비즈니스 로직 서비스
   @RequiredArgsConstructor  // ← 이전에 배운 개념! 의존성 자동 주입
   ```

2. **비즈니스 로직 - 회원가입**
   ```java
   public Long join(Member member) {
       // 이전에 배운 BCrypt 개념이 여기서 사용됨!
       String hashedPassword = BCrypt.hashpw(
           member.getPassword(),  // 평문 비밀번호
           BCrypt.gensalt()       // Salt 자동 생성
       );
       member.setPassword(hashedPassword);  // 해싱된 비밀번호로 변경
       
       memberRepository.save(member);  // Repository에 저장 요청
       return member.getId();
   }
   ```

3. **비즈니스 로직 - 로그인**
   ```java
   public Member login(String username, String password) {
       Member member = memberRepository.findByUsername(username);
       
       // 이전에 배운 BCrypt 검증 개념!
       if (member != null && 
           BCrypt.checkpw(password, member.getPassword())) {
           return member;  // 로그인 성공
       }
       return null;  // 로그인 실패
   }
   ```

**실습 포인트:**
- "비즈니스 로직이 여기 있다는 것을 확인해봅시다!"
- "BCrypt 개념이 실제 코드에서 어떻게 사용되는지 보세요!"
- "Controller와의 차이점을 느껴보세요!"

**코드 스크린샷:**
- `MemberService.java` 파일 전체
- `join()` 메서드 (비밀번호 해싱 부분 강조)
- `login()` 메서드 (BCrypt 검증 부분 강조)

---

### **슬라이드 13: Repository 계층 코드 실습 (창고지기)**

**제목:** Repository 계층 코드 실습: 창고지기의 역할 📦

**비유 상기:**
> "창고지기는 셰프가 요청한 재료를 냉장고에서 꺼내주고, 사용한 재료를 정리합니다."

**이제 실제 코드를 봅시다!**

**코드 스크린샷:**
```java
@Repository                       // ← 이 클래스는 저장소 계층!
@Slf4j                            // ← 로깅 기능
public class MemberRepositoryImpl implements MemberRepository {
    
    // 메모리 저장소 (실무에서는 DB 사용)
    private final Map<Long, Member> store = new ConcurrentHashMap<>();
    
    // 파일에서 데이터 로드 (애플리케이션 시작 시)
    public MemberRepositoryImpl() {
        loadDataFromFile();
    }
    
    @Override
    public void save(Member member) {
        if (member.getId() == null) {
            // 신규 저장: ID 자동 생성
            member.setId(sequence.incrementAndGet());
        }
        store.put(member.getId(), member);
        saveDataToFile(); // 파일에 저장
    }
    
    @Override
    public Member findOne(Long id) {
        return store.get(id); // 메모리에서 조회
    }
}
```

**설명 포인트:**
- `@Repository`: 스프링이 이 클래스를 저장소로 인식
- 실제 데이터 접근 로직이 여기 있음!
- 교육용으로 JSON 파일 사용 (실무에서는 DB)

**시각적 효과:**
- ConcurrentHashMap 설명
- 파일 저장/로드 흐름 다이어그램

---

**제목:** 실제 프로젝트 구조를 살펴봅시다! 📁

**프로젝트 트리 구조:**
```
src/main/java/com/example/demo/
├── controller/
│   └── MemberController.java    ← 웨이터 (요청 처리)
├── service/
│   └── MemberService.java       ← 셰프 (비즈니스 로직)
├── repository/
│   ├── MemberRepository.java    ← 창고 인터페이스
│   └── MemberRepositoryImpl.java ← 창고지기 (데이터 접근)
├── domain/
│   └── Member.java              ← 회원 엔티티 (데이터 모델)
└── DTO/
    ├── CreateMemberRequest.java  ← 요청용 DTO
    └── MemberResponse.java      ← 응답용 DTO
```

**설명:**
- 각 폴더가 계층을 나타냄
- `domain`: 실제 데이터 구조 (Member)
- `DTO`: 계층 간 데이터 전달용 객체

**코드 스크린샷 위치:**
- IDE의 프로젝트 트리 뷰
- 각 폴더를 클릭해서 보여주기

**실습 포인트:**
- "이제 이 구조를 코드로 확인해봅시다!"

---

### **슬라이드 11: Controller 계층 코드 실습 (웨이터)**

**제목:** 실제 API가 동작하는 흐름을 따라가봅시다! 🔄

**시나리오:** 회원가입 API 호출

**다이어그램:**
```
1. 클라이언트 요청
   POST /api/members
   {
     "username": "testuser",
     "password": "1234",
     "name": "테스트"
   }
   ↓
2. Controller (웨이터)
   - 요청 받기
   - DTO → Member 변환
   ↓
3. Service (셰프)
   - 비밀번호 해싱 (BCrypt)
   - 비즈니스 로직 처리
   ↓
4. Repository (창고지기)
   - ID 자동 생성
   - 메모리에 저장
   - JSON 파일에 저장
   ↓
5. 응답 반환
   {
     "data": 1  // 생성된 회원 ID
   }
```

**코드 흐름 설명:**

**1단계: Controller에서 요청 받기**
```java
@PostMapping("/members")
public Result<Long> saveMember(@RequestBody CreateMemberRequest request) {
    Member member = new Member();
    member.setUsername(request.getUsername());
    member.setPassword(request.getPassword());  // ← 아직 평문
    member.setName(request.getName());
    
    Long id = memberService.join(member);  // ← Service 호출
    return new Result<>(id);
}
```

**2단계: Service에서 비즈니스 로직 처리**
```java
public Long join(Member member) {
    // 비밀번호 해싱
    String hashedPassword = BCrypt.hashpw(
        member.getPassword(), 
        BCrypt.gensalt()
    );
    member.setPassword(hashedPassword);  // ← 해싱된 비밀번호로 변경
    
    memberRepository.save(member);  // ← Repository 호출
    return member.getId();
}
```

**3단계: Repository에서 데이터 저장**
```java
public void save(Member member) {
    member.setId(sequence.incrementAndGet());  // ← ID 자동 생성
    store.put(member.getId(), member);        // ← 메모리에 저장
    saveDataToFile();                         // ← 파일에 저장
}
```

**실습 포인트:**
- "각 계층이 어떻게 협력하는지 확인해봅시다!"
- "데이터가 어떻게 변환되는지 따라가봅시다!"
- "이전에 배운 개념들이 실제로 어떻게 동작하는지 보세요!"

**코드 스크린샷:**
- 각 단계별로 해당 코드 파일 보여주기
- 실제 JSON 요청/응답 예시

**시각적 효과:**
- 각 단계를 번호와 색상으로 구분
- 화살표로 데이터 흐름 표시
- 실제 JSON 요청/응답 예시 포함

---

### **슬라이드 15: 프로젝트에서 사용하는 기술 스택**

**제목:** 이 프로젝트에서 사용하는 기술들 🛠️

**기술 스택:**

| 카테고리 | 기술 | 용도 |
|:---|:---|:---|
| **프레임워크** | Spring Boot 3.4.1 | 백엔드 프레임워크 |
| **언어** | Java 21 | 프로그래밍 언어 |
| **빌드 도구** | Gradle | 의존성 관리 및 빌드 |
| **보안** | BCrypt (jBCrypt) | 비밀번호 해싱 |
| **인증** | JWT (JJWT 0.9.1) | 토큰 기반 인증 |
| **데이터 저장** | JSON 파일 | 교육용 데이터 저장소 |
| **JSON 처리** | Jackson | JSON 파싱/생성 |
| **코드 간소화** | Lombok | 반복 코드 제거 |

**코드 스크린샷:**
- `build.gradle` 파일 (의존성 목록)

**설명:**
- 각 기술이 왜 필요한지 간단히 설명
- 실무에서는 DB를 사용하지만, 교육용으로 JSON 파일 사용

---

### **슬라이드 16: 마무리 및 학습 로드맵**

**제목:** 다음 단계: Spring Boot 정복하기! 🚀

**오늘 배운 내용 요약:**

**📚 개념 학습 (Part 1)**
1. ✅ Spring Boot의 핵심 개념 이해
2. ✅ 계층형 아키텍처 (Controller-Service-Repository)
3. ✅ 필수 어노테이션 활용
4. ✅ Lombok으로 코드 간소화
5. ✅ BCrypt 비밀번호 해싱 개념

**💻 실습 프로젝트 (Part 2)**
6. ✅ 실제 프로젝트 구조 이해
7. ✅ 각 계층의 코드 분석
8. ✅ API 동작 흐름 이해

**학습 로드맵:**

```
1단계: Java 기본기
  └─ Interface, 다형성, 제네릭 완벽 이해

2단계: HTTP & REST API
  └─ 웹이 동작하는 기본 원리 학습

3단계: JPA (ORM)
  └─ SQL 없이 자바 코드로 DB 다루기

4단계: Mini Project
  └─ 게시판(CRUD)을 처음부터 끝까지 만들기
```

**핵심 메시지:**
> 💡 **"백엔드 개발은 데이터가 들어와서(Controller),  
> 가공되고(Service), 저장되는(Repository) 흐름을 제어하는 예술입니다."**

**질문 시간:**
- Q&A
- 프로젝트 코드 리뷰

**연락처:**
- GitHub 링크
- 이메일

---

## 📸 코드 스크린샷 촬영 가이드

### 촬영해야 할 파일들:

1. **프로젝트 구조**
   - IDE의 프로젝트 트리 전체
   - 각 폴더별로 확대

2. **Controller**
   - `MemberController.java` 전체
   - 각 메서드별로 확대 (회원가입, 조회, 수정, 삭제, 로그인)

3. **Service**
   - `MemberService.java` 전체
   - `join()` 메서드 (비밀번호 해싱 부분 강조)
   - `login()` 메서드 (BCrypt 검증 부분 강조)

4. **Repository**
   - `MemberRepositoryImpl.java` 전체
   - `save()` 메서드
   - `loadDataFromFile()` 메서드

5. **Domain & DTO**
   - `Member.java` (Lombok 사용 예시)
   - `CreateMemberRequest.java` (`@Data` 사용 예시)
   - `MemberResponse.java`

6. **설정 파일**
   - `build.gradle` (의존성 목록)
   - `application.properties`

7. **데이터 파일**
   - `data/members.json` (해싱된 비밀번호 예시)

### 촬영 팁:
- 코드 하이라이팅 활성화
- 중요한 부분은 주석으로 표시
- 라인 번호 표시
- 폰트 크기 충분히 크게 (최소 12pt 이상)

---

## 🎨 디자인 가이드

### 색상 팔레트:
- **Controller**: 분홍색 계열 (#f9f)
- **Service**: 파란색 계열 (#ccf)
- **Repository**: 노란색 계열 (#ff9)
- **배경**: 흰색 또는 연한 회색
- **강조**: 빨간색 또는 주황색

### 폰트:
- **제목**: 굵은 고딕체 (예: 나눔고딕 Bold)
- **본문**: 일반 고딕체 (예: 나눔고딕)
- **코드**: 고정폭 폰트 (예: Consolas, D2Coding)

### 레이아웃:
- 한 슬라이드에 너무 많은 내용 X
- 코드는 읽기 쉽게 포맷팅
- 다이어그램은 간결하게

---

## 💡 발표 팁

1. **비유 활용**: 식당 비유를 계속 언급하며 일관성 유지
2. **코드 설명**: 코드를 읽어가며 각 부분 설명
3. **질문 유도**: "왜 이렇게 했을까요?" 같은 질문으로 참여 유도
4. **실습 제안**: "이 코드를 직접 실행해보세요" 같은 액션 아이템 제시
5. **속도 조절**: 초보자를 위해 천천히, 반복 설명

---

## ✅ 체크리스트

- [ ] 모든 슬라이드 작성 완료
- [ ] 코드 스크린샷 촬영 완료
- [ ] 다이어그램 작성 완료
- [ ] 발표 연습 (시간 체크)
- [ ] Q&A 준비
- [ ] 프로젝트 실행 확인

---

**작성일:** 2024년  
**버전:** 1.0
