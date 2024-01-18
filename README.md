<h1>
 나만의 Mp3 Player🎵
</h1>

<p align="center">  
  
![image](https://github.com/AnMyungwoo94/Mp3PlayerOnDB/assets/126849689/20ab96e2-c6bf-402c-b5f7-36845a397052)

</p> 

### <img src="https://github.com/AnMyungwoo94/BeautyIdea_Shopping_App/assets/126849689/d0ba6eb9-f5c2-4839-ad30-6f8bf65c7452" alt="구글 플레이 이미지" width="30" height="" style="float:left"> [ 앱 다운로드 링크 ](https://play.google.com/store/apps/details?id=com.myungwoo.mp3playerondb)

## 1. 소개
- 휴대폰에 다운로드한 음악을 재생할 수 있습니다.
- 재생, 다음곡, 이전곡, 셔플 등의 기능이 지원됩니다.
- 웹뷰를 통해 유튜브 링크로 연결됩니다.
- 음악 검색 및 좋아요 기능이 가능합니다.
- 지정 된 asmr음악 감상이 가능합니다.

## 2. 최근 업데이트
- 23/12/31일 신규앱 기능 설명 BottomSheetDialogFragment 추가(아래 사진 참고)
- 23/11/10일 녹음기능 및 녹음 파동 추가 (녹음파일 저장기능은 추가예정)
- 23/11/09일 상단 음악 제어바 추가(Notification) 및 수정필요
- 23/08/17일 Google Play 배포 완료
<img src="https://github.com/AnMyungwoo94/Mp3PlayerOnDB/assets/126849689/9c6a081f-5033-4c78-a640-98ab60d4a3d3" alt="다이얼로그 프래그먼트" width="300" height="600">
<img src="https://github.com/AnMyungwoo94/Mp3PlayerOnDB/assets/126849689/19c1817c-3d92-4ee0-a6eb-b1de199eb508" alt="다이얼로그 프래그먼트" width="300" height="600"> 

</br>

## 3. 기능소개 
![mp3player_와이어프레임](https://github.com/AnMyungwoo94/Mp3PlayerOnDB/assets/126849689/c16104cb-d95e-47bf-a466-794921f19ffa)

|스플래시, 재생목록, 검색, 좋아요|재생 화면| 웹뷰화면|
|:-----:|:-----:|:-----:|
|<img width="250" src="https://github.com/AnMyungwoo94/Mp3PlayerOnDB/assets/126849689/97f6cee8-10b3-4003-a654-ed00ce2c5cf8.gif">|<img width="250" src="https://github.com/AnMyungwoo94/Mp3PlayerOnDB/assets/126849689/17052bda-deb4-4b3a-b6a0-885ecd17882e.gif">|<img width="250" src="https://github.com/AnMyungwoo94/Mp3PlayerOnDB/assets/126849689/3bcea210-9779-47aa-b817-e1360a6e32bd.gif" />|

## 4. 기술스택
- MediaPlayer 
- MediaRecorder 
- SQLite Database
- Coroutines

## 5. 프로젝트 마무리 하며 느낀 점
- 내 첫 앱 프로젝트인 만큼 뿌듯하면서도 부족한 점이 많았다. 
SQLite에 대해 새롭게 알 수 있었으며, UI디자인을 조금 더 깔끔하게 하면 좋았을것이라는 아쉬움이 남았다. 
메인페이지에는 RecyclerView 두개를 넣어서 진행했지만 맨 위에것은 티가 나지 않아 설명 혹은 포인트를 주어 사용자가 옆으로 넘길 수 있도록 해야겠다.

## 6. 개선사항
- 음악 제어바를 추가했으나 개선이 필요해 보여 삭제함. 추후 진행 예정
- 음성파일 다운로드 기능 구현하기
- 비디오 기능 추가하기
