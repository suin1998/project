const API_URL = 'http://localhost:8080/AI/weather';

const $siDo = document.getElementById('sido');
const $siGunGu = document.getElementById('sigungu');
const $dong = document.getElementById('dong');
const $datePicker = document.getElementById('datePicker');
const $weatherDisplay = document.getElementById('weatherDisplay');
const $checkWeatherBtn = document.getElementById('checkBtn');
// 선택된 지역 및 날짜 저장 변수
let selectedRegion = {
    siDo: '',
    siGunGu: '',
    dong: ''
};
let selectedDate = '';

document.addEventListener('DOMContentLoaded', () => {
    // ... (기존 초기화 함수 호출: setupDropdownToggles(), setupFlatpickr(), setupRegionEventListeners() 등) ...

    // 💡 날씨 조회 버튼 이벤트 리스너 추가
    $checkWeatherBtn.addEventListener('click', fetchWeather);

    // 초기 날짜 및 지역 값 설정 (HTML에서 값을 가져오도록)
    selectedDate = $datePicker.value;
    selectedRegion.siDo = $siDo.value;
    selectedRegion.siGunGu = $siGunGu.value;
    selectedRegion.dong = $dong.value;
});


function isWeatherQueryReady() {
    return selectedRegion.siDo && selectedRegion.siGunGu && selectedRegion.dong && selectedDate;
}

async function fetchWeather() {
    if (!isWeatherQueryReady()) return;

    // 1. 요청 URL 구성
    const params = new URLSearchParams({
        siDo: selectedRegion.siDo,
        siGunGu: selectedRegion.siGunGu,
        dong: selectedRegion.dong,
        inputDate: selectedDate
    });

    const fullUrl = `${API_URL}?${params.toString()}`;
    $weatherDisplay.innerHTML = '<p>날씨 정보 조회 중...</p>';

    try {
        const response = await fetch(fullUrl);
        const data = await response.json();

        if (response.ok) {
            // 200 OK
            displayWeatherResult(data);
        } else {
            // 4xx, 5xx 에러 처리
            $weatherDisplay.innerHTML = `<p class="error">조회 실패: ${data.message || 'API 호출에 실패했습니다.'}</p>`;
            console.error('API Error Response:', data);
        }
    } catch (error) {
        $weatherDisplay.innerHTML = `<p class="error">네트워크 오류 또는 JSON 파싱 오류</p>`;
        console.error('Fetch Error:', error);
    }
}
function displayWeatherResult(data) {
    let html = `<p><strong>📍 ${data.targetDate} (${selectedRegion.siDo} ${selectedRegion.siGunGu} ${selectedRegion.dong})</strong></p>`;

    if (data.shortTerm) {
        const s = data.shortTerm;
        html += `
            <div class="weather-forecast short-term">
                <p>최저 기온: ${s.tMin}°C</p>
                <p>최고 기온: ${s.tMax}°C</p> 
                <p>강수 확률 (POP): ${s.rainProb}%</p>
            </div>
        `;
    } else if (data.midTerm) {
        const m = data.midTerm;
        html += `
            <div class="weather-forecast mid-term">
                <p>최저 기온: ${m.tMin}°C</p>
                <p>최고 기온: ${m.tMax}°C</p>
                <p>강수 확률: ${m.rainProb}%</p>
                </div>
        `;
    } else {
        html += '<p>해당 날짜에 유효한 날씨 데이터가 없습니다.</p>';
    }

    $weatherDisplay.innerHTML = html;
}
