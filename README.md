<img src="./images/logo.png" alt="Logo of the project" align="right">

# 개발중

# valueFinder

> 사용자의 물건에 가치를 찾아주는 개인간 경매 서비스

## 소개:

ValueFinder는 사용자들이 소유한 물건의 정확한 가치를 찾아주는 개인간 경매 서비스입니다. 많은 사람들이 소유한 물건의 가치를 모르거나 판매하기 어려워하는 경우가 있습니다. 우리는 이러한 문제를 해결하고자 ValueFinder를 개발하였습니다.

> 물품 등록과 경매: 사용자들은 물품을 등록하고 경매에 참여할 수 있습니다. 역경매 방식도 지원하여 판매자가 물품을 게시하면 구매자들이 경매 가격을 제시하고 최고 입찰자가 낙찰됩니다.

> 매물 노출과 카테고리 분류: 메인화면에서는 인기 경매 매물, 마감 임박 매물, 신규 매물 등을 볼 수 있으며, 카테고리 별로 매물을 분류하여 검색이 용이합니다.

> 낙찰 이후 과정: 낙찰된 사용자들은 판매자와의 거래 진행 상태를 확인하고 운송장번호를 등록할 수 있으며, 거래가 완료된 후 후기를 작성할 수 있습니다.

## 팀원

<table>
  <tbody>
    <tr>
      <td align="center"><a href="https://github.com/Lee-hye-won"><img src="https://avatars.githubusercontent.com/u/105015513?v=4" width="100px;" alt=""/><br /><sub><b>팀장 : 이혜원</b></sub></a><br /></td>
      <td align="center"><a href="https://github.com/minwoochae"><img src="https://avatars.githubusercontent.com/u/130428663?v=4" width="100px;" alt=""/><br /><sub><b>팀원 : 채민우</b></sub></a><br /></td>
      <td align="center"><a href="https://github.com/leejiheee"><img src="https://avatars.githubusercontent.com/u/130732295?v=44" width="100px;" alt=""/><br /><sub><b>팀원 : 이지희</b></sub></a><br /></td>
      <td align="center"><a href="https://github.com/jhkim1102"><img src="https://avatars.githubusercontent.com/u/130732119?v=4" width="100px;" alt=""/><br /><sub><b>팀원 : 김종화</b></sub></a><br /></td>
      <td align="center"><a href="https://github.com/JOONGHWANLEE"><img src="https://avatars.githubusercontent.com/u/130732039?v=4" width="100px;" alt=""/><br /><sub><b>팀원 : 이중환</b></sub></a><br /></td>
    </tr>
  </tbody>
</table>
<hr>

### [참고 HTML템플릿](https://themewagon.github.io/travelo/)

### [요구사항 정의서](https://docs.google.com/spreadsheets/d/150WOxxBdb120Stql9RnWlvUK_vRlqHGH37nTVox6e8Y/edit#gid=0)

### [간트 차트](https://docs.google.com/spreadsheets/d/1WsOu9WPZ1T770cEXPiNKnxmyKw5zxnEFisGoHuBn0dU/edit?pli=1#gid=1115838130)

### [ERD](https://www.erdcloud.com/d/AJECkdDa5fRg4ioaw)

### [화면설계](https://www.figma.com/file/JXdW6QzP7QEJbPIjqog2jp/Untitled?type=design&node-id=0-1&mode=design&t=Fxcg4JCad8AJw5UW-0)

<hr>

## 📚 목차

* [작업 규칙](#작업-규칙)

<hr>

## 작업 규칙

### 커밋, 브랜치 네이밍 룰

#### 커밋

➕add : 새로운 기능에 대한 커밋

🔧fix : 잘못된 부분 수정

💣build : 빌드 관련 파일 수정에 대한 커밋(application.properties, pom.xml ...)

📝chore : 그 외 자잘한 수정에 대한 커밋(기타 변경)

🔖docs : 문서 수정에 대한 커밋

💥rm : 기능 삭제

👻refactor : 코드 리팩토링에 대한 커밋

🌀ing : 개발 중 커밋 (커밋 기준 ~~ 완료 / ~~ 미완료)

🎉complete : 기능 구현 완료에 대한 커밋

ex)
➕add : 로그인 기능 add

🌀ing : 로그인 기능 완료 / redirect url 미완료

<hr>

#### 브랜치

> `<strong>`절대 main 브랜치에 push 하지 않습니다!! develop 브랜치에 push 합니다!!`</strong>`

##### 브랜치 네이밍 규칙

feature/#{리퀘스트 번호}-{기능분류}-{기능명}

```
ex)
feature/#1-member-login
```

##### 풀 리퀘스트 네이밍 규칙

[#번호] 내용

```
ex)
[#1] 로그인 기능 구현
```

##### 로컬 저장소(내 컴퓨터) 에서 브랜치 생성하는 법

```git
프로젝트 루트 폴더에서
git branch {브랜치명} // 브랜치 생성
git checkout {브랜치명} // 브랜치 이동
```

##### 개발한 브랜치를 push 하고 싶어요

```git
git branch // 현재 작업중인 브랜치 확인

  develope
  main
* feature/#1-member-login   // *이 붙어있는 곳이 현재 작업중인 브랜치


git add .                                 // 트래킹 중이지 않은 파일 추가
git commit -m ":cyclone:ing : 로그인 기능 완료 / redirect url 미완료"       // 커밋 메시지 작성
git push origin feature/#1-member-login   // 작업이 끝나지 않았다면
git push origin feature/develop           // 작업이 끝났다면
```

##### push 한 후 쓰지 않는 로컬 브랜치를 지우고 싶어요

```git
git branch -d {브랜치명}
```

##### 깃허브에 있는 브랜치를 내려받고 싶어요

```git
git branch // 현재 작업중인 브랜치 확인

  develope
  main
* feature/#1-member-login   // *이 붙어있는 곳이 현재 작업중인 브랜치

git pull origin {내려받을 브랜치명}
```

<hr>

### 코드 네이밍 룰

#### 모든 자바 메소드명, 변수명은 카멜 케이스를 따릅니다.

#### 또한 누구나 알기 쉬운 단어를 사용합니다.

메소드명은 동사로 네이밍합니다.

👍

```java
private String personName; 

public void getUserId() {

}
```

👎

```java
private String PersonName;
private String personname; 

public void userid() {

}
```

#### 클래스 명은 파스칼 케이스를 따릅니다.

👍

```text
SampleCode.java
SampleCodeDto.java
```

👎

```text
samplecode.java
sampleCodeDto.java
```

#### HTML 파일 명, 패키지명 은 모두 소문자를 사용합니다.

👍

```text
samplecode.html
```

👎

```text
sample_code.html
sampleCode.html
```

#### 패키지명은 모두 소문자를 사용합니다.

#### ENUM이나 상수는 대문자로 네이밍합니다.
