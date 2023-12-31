// SELECT BOX 선택
var areaCodeSelect = document.getElementById("areaCodeSelect");
var contentTypeIdSelect = document.getElementById("contentTypeIdSelect");

// 결과를 표시할 영역 선택
var resultsDiv = document.getElementById("results");

// 검색 버튼 선택
var searchButton = document.getElementById("searchButton");

// 사용자가 지역 코드 선택할 때의 이벤트 리스너 추가
areaCodeSelect.addEventListener("change", updateContentTypes);

// 검색 버튼 클릭 시 결과 업데이트
searchButton.addEventListener("click", updateResults);

// 초기 로드시 지역 코드 목록을 가져옴
fetch(
  "https://apis.data.go.kr/B551011/KorService1/areaCode1?numOfRows=20&pageNo=1&MobileOS=ETC&MobileApp=TEST&serviceKey=bWi7itZDsVW8U1exI%2BALv2Eys5Aq6ELHC0tumPmSeA%2Bb221ygrItwTu0OKj%2BXDcb61FoPzn5Ut7PlCRAHy94Zw%3D%3D"
)
  .then((response) => response.text())
  .then((data) => {
    var parser = new DOMParser();
    var xmlDoc = parser.parseFromString(data, "text/xml");

    var items = xmlDoc.getElementsByTagName("item");

    for (var i = 0; i < items.length; i++) {
      var item = items[i];
      var code = item.getElementsByTagName("code")[0].textContent;
      var name = item.getElementsByTagName("name")[0].textContent;

      // OPTION 요소 생성 및 추가
      var option = document.createElement("option");
      option.value = code;
      option.text = name;
      areaCodeSelect.appendChild(option);
    }

    // 초기 로드시 지역 코드를 기반으로 콘텐츠 유형 업데이트
    // updateContentTypes();
  })
  .catch((error) => console.error("지역 코드 호출 오류:", error));

// 지역 코드 선택 시 콘텐츠 유형 목록을 가져오고 셀렉트 박스 업데이트
function updateContentTypes() {
  contentTypeIdSelect.innerHTML = ""; // 콘텐츠 유형 목록 초기화

  var selectedAreaCode = areaCodeSelect.value;
  var contentTypes = [
    {
      id: 1,
      name: "식당",
      url:
        "https://apis.data.go.kr/B551011/KorService1/areaBasedList1?numOfRows=10&pageNo=1&MobileOS=ETC&MobileApp=TEST&arrange=O&contentTypeId=39&areaCode=" +
        selectedAreaCode +
        "&serviceKey=bWi7itZDsVW8U1exI%2BALv2Eys5Aq6ELHC0tumPmSeA%2Bb221ygrItwTu0OKj%2BXDcb61FoPzn5Ut7PlCRAHy94Zw%3D%3D",
    },
    {
      id: 2,
      name: "숙소",
      url:
        "https://apis.data.go.kr/B551011/KorService1/searchStay1?numOfRows=10&pageNo=1&MobileOS=ETC&MobileApp=TEST&areaCode=" +
        selectedAreaCode +
        "&serviceKey=bWi7itZDsVW8U1exI%2BALv2Eys5Aq6ELHC0tumPmSeA%2Bb221ygrItwTu0OKj%2BXDcb61FoPzn5Ut7PlCRAHy94Zw%3D%3D",
    },
    {
      id: 3,
      name: "관광지",
      url:
        "https://apis.data.go.kr/B551011/KorService1/areaBasedSyncList1?numOfRows=10&pageNo=1&MobileOS=ETC&MobileApp=TEST&serviceKey=bWi7itZDsVW8U1exI%2BALv2Eys5Aq6ELHC0tumPmSeA%2Bb221ygrItwTu0OKj%2BXDcb61FoPzn5Ut7PlCRAHy94Zw%3D%3D&listYN=Y&arrange=A&contentTypeId=12&areaCode=" +
        selectedAreaCode,
    },
  ];

  for (var i = 0; i < contentTypes.length; i++) {
    var option = document.createElement("option");
    option.value = contentTypes[i].id;
    option.text = contentTypes[i].name;
    contentTypeIdSelect.appendChild(option);
  }

  // 초기 로드시 콘텐츠 결과 업데이트
  //   updateResults();
}

// 콘텐츠 유형 선택 시 결과 업데이트
function updateResults() {
  resultsDiv.innerHTML = ""; // 결과 영역 초기화

  var selectedContentTypeId = contentTypeIdSelect.value;
  var selectedAreaCode = areaCodeSelect.value;

  var contentTypes = {
    1: "39",
    2: "32",
    3: "12",
  };

  var apiUrl = `https://apis.data.go.kr/B551011/KorService1/areaBasedList1?numOfRows=10&pageNo=1&MobileOS=ETC&MobileApp=TEST&arrange=O&contentTypeId=${contentTypes[selectedContentTypeId]}&areaCode=${selectedAreaCode}&serviceKey=bWi7itZDsVW8U1exI%2BALv2Eys5Aq6ELHC0tumPmSeA%2Bb221ygrItwTu0OKj%2BXDcb61FoPzn5Ut7PlCRAHy94Zw%3D%3D`;

  fetch(apiUrl)
    .then((response) => response.text())
    .then((data) => {
      var parser = new DOMParser();
      var xmlDoc = parser.parseFromString(data, "text/xml");

      var items = xmlDoc.getElementsByTagName("item");

      for (var i = 0; i < items.length; i++) {
        var item = items[i];
        var title = item.getElementsByTagName("title")[0].textContent;
        var address = item.getElementsByTagName("addr1")[0].textContent;
        var tel = item.getElementsByTagName("tel")[0].textContent;
        var firstImage = item.getElementsByTagName("firstimage")[0].textContent;

        //위도 경도
        //  var mapx = item.getElementsByTagName("mapx")[0].textContent;
        //  var mapy = item.getElementsByTagName("mapy")[0].textContent;
        //   <p>Mapx: ${mapx}</p>
        //   <p>Mapy: ${mapy}</p>

        var resultElement = document.createElement("div");
        resultElement.innerHTML = `
                    <h3>${title}</h3>
                    <p>${address}</p>
                    <p>Tel: ${tel}</p>
                    <img src="${firstImage}" style="width:500px; height:300px;" alt="${title} Image">
                `;

        resultsDiv.appendChild(resultElement);
      }
    })
    .catch((error) => console.error("API 호출 오류:", error));
}

var map;

// 카카오맵 생성
function initMap() {
  var center = new kakao.maps.LatLng(37.5665, 126.978); // 초기 중심 좌표
  map = new kakao.maps.Map(document.getElementById("map"), {
    center: center,
    level: 8,
  });
}

// 마커 생성 및 추가
function addMarker(mapx, mapy) {
  var markerPosition = new kakao.maps.LatLng(mapy, mapx);
  var marker = new kakao.maps.Marker({
    position: markerPosition,
  });
  marker.setMap(map);
}

// 결과 이미지 클릭 시 마커 추가
function addMarkerOnClick(imageElement) {
  imageElement.addEventListener("click", function () {
    var mapx = parseFloat(imageElement.getAttribute("data-mapx"));
    var mapy = parseFloat(imageElement.getAttribute("data-mapy"));
    addMarker(mapx, mapy);
  });
}

window.onload = function () {
  initMap();
  var imageElements = document.querySelectorAll(".result-image");
  imageElements.forEach(function (element) {
    addMarkerOnClick(element);
  });
};