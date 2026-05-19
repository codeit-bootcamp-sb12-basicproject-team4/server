package com.codeit.findex.autointegration.service; //

import com.codeit.findex.autointegration.entity.AutoIntegration;
import com.codeit.findex.autointegration.repository.AutoIntegrationRepository;
import com.codeit.findex.integrationlog.service.IntegrationFacade; //
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoIntegrationScheduler {

  private final AutoIntegrationRepository autoIntegrationRepository;
  private final IntegrationFacade integrationFacade; //


  @Scheduled(cron = "0 0 2 * * ?") // 매일 새벽 2시 정기 실행
  public void runAutoIntegrationBatch() {
    log.info(" [배치 스타트] 주기적 지수 데이터 자동 연동 스케줄러를 시작합니다.");


    List<AutoIntegration> activeConfigs = autoIntegrationRepository.findByIsActiveTrue();

    if (activeConfigs.isEmpty()) {
      log.info("현재 활성화된 지수 연동 설정이 없어 배치를 진행하지 않고 종료합니다.");
      return;
    }

    log.info("활성화된 연동 설정 확인 완료 ({}개). 전체 동기화 프로세스를 시작합니다.", activeConfigs.size());

    try {
      integrationFacade.syncIndexInfo("system"); //

      log.info("모든 활성화 지수에 대한 데이터 최신화 및 연동 로그 기록 성공!");
    } catch (Exception e) {
      log.error("자동 연동 배치 중 에러 발생: {}", e.getMessage());
    }

    log.info("[배치 종료] 지수 데이터 자동 연동 작업이 끝났습니다.");
  }
}