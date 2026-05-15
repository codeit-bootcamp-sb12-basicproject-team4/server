-- 초기화
DROP SCHEMA IF EXISTS findex CASCADE;

CREATE SCHEMA findex;
SET search_path TO findex;
COMMIT;

CREATE TABLE findex -- ERD의 index -> findex로 변경
(
    id UUID PRIMARY KEY,
    index_name VARCHAR(100) NOT NULL,
    index_classification VARCHAR(50) NOT NULL,
    items_count INT,
    base_pntm DATE NOT NULL,
    base_index DECIMAL(18, 2) NOT NULL,
    source_type VARCHAR(20) NOT NULL, -- "USER" 또는 "OPEN_API"
    favorite BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT chk_findex_source_type CHECK (source_type IN ('USER', 'OPEN_API')), -- 상수 대문자 통일
    CONSTRAINT uk_findex_name_class UNIQUE (index_classification, index_name)
);

COMMENT ON TABLE findex IS '지수';
COMMENT ON COLUMN findex.index_name IS '지수명';
COMMENT ON COLUMN findex.index_classification IS '지수분류명';
COMMENT ON COLUMN findex.items_count IS '채용종목수';
COMMENT ON COLUMN findex.base_pntm IS '기준시점 (pntm:point_in_time)';
COMMENT ON COLUMN findex.base_index IS '기준시점';
COMMENT ON COLUMN findex.source_type IS '소스타입 ("USER", "OPEN_API")';
COMMENT ON COLUMN findex.favorite IS '즐겨찾기';
COMMENT ON COLUMN findex.created_at IS '생성일시';
COMMENT ON COLUMN findex.updated_at IS '수정일시';


CREATE TABLE index_data
(
    id UUID PRIMARY KEY,
    findex_id UUID NOT NULL, -- ERD의 id -> findex_id 로 변경
    base_date DATE NOT NULL,
    source_type VARCHAR(20) NOT NULL, -- "USER" 또는 "OPEN_API"
    market_price DECIMAL(18, 2) NOT NULL,
    close_price DECIMAL(18, 2) NOT NULL,
    high_price DECIMAL(18, 2) NOT NULL,
    low_price DECIMAL(18, 2) NOT NULL,
    versus DECIMAL(18, 2) NOT NULL,
    fluctuation_rate DECIMAL(5, 2) NOT NULL,
    trading_quantity BIGINT,
    trading_price BIGINT,
    market_totalamount BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_index_data_findex FOREIGN KEY (findex_id) REFERENCES findex (id) ON DELETE CASCADE,
    CONSTRAINT chk_index_data_source CHECK (source_type IN ('USER', 'OPEN_API')), -- 상수 대문자 통일
    CONSTRAINT uk_index_data_findex_date UNIQUE (findex_id, base_date)

);

COMMENT ON TABLE index_data IS '지수 데이터';
COMMENT ON COLUMN index_data.id IS '지수데이터ID (PK)';
COMMENT ON COLUMN index_data.findex_id IS '지수ID (FK)';
COMMENT ON COLUMN index_data.base_date IS '기준일자';
COMMENT ON COLUMN index_data.source_type IS '소스타입 ("USER", "OPEN_API")';
COMMENT ON COLUMN index_data.market_price IS '시가';
COMMENT ON COLUMN index_data.close_price IS '종가';
COMMENT ON COLUMN index_data.high_price IS '고가';
COMMENT ON COLUMN index_data.low_price IS '저가';
COMMENT ON COLUMN index_data.versus IS '대비';
COMMENT ON COLUMN index_data.fluctuation_rate IS '등락률';
COMMENT ON COLUMN index_data.trading_quantity IS '거래량';
COMMENT ON COLUMN index_data.trading_price IS '거래대금';
COMMENT ON COLUMN index_data.market_totalamount IS '상장시가총액';
COMMENT ON COLUMN index_data.created_at IS '생성일시';
COMMENT ON COLUMN index_data.updated_at IS '수정일시';

CREATE TABLE integration_log (
    id UUID PRIMARY KEY,
    job_type VARCHAR(20) NOT NULL,
    target_date DATE NOT NULL,
    worker VARCHAR(50) NOT NULL,
    job_time TIMESTAMP NOT NULL,
    result VARCHAR(10) NOT NULL,
    findex_id UUID NOT NULL,                            -- ERD의 id -> findex_id 로 변경
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_integration_log_findex FOREIGN KEY (findex_id) REFERENCES findex (id) ON DELETE CASCADE,
    CONSTRAINT chk_log_job_type CHECK (job_type IN ('INDEX', 'DATA')),  -- 상수 대문자 통일
    CONSTRAINT chk_log_result CHECK (result IN ('SUCCESS', 'FAIL')) -- 상수 대문자 통일
);
COMMENT ON TABLE integration_log IS '연동 작업 로그';
COMMENT ON COLUMN integration_log.id IS '연동작업ID (PK)';
COMMENT ON COLUMN integration_log.job_type IS '유형 ("index", "data")';
COMMENT ON COLUMN integration_log.target_date IS '대상날짜 (연동된 지수의 기준시점(findex) or 기준일자(index_data))';
COMMENT ON COLUMN integration_log.job_time IS '작업일시 (연동 작업이 수행된 일시)';
COMMENT ON COLUMN integration_log.result IS '결과 ("success", "fail")';
COMMENT ON COLUMN integration_log.findex_id IS '지수ID (FK)';
COMMENT ON COLUMN integration_log.created_at IS '생성일시';

CREATE TABLE auto_integration (
    id UUID PRIMARY KEY,                                -- UUIDv7
    findex_id UUID UNIQUE NOT NULL,                            -- ERD의 id -> findex_id 로 변경
    is_active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL, -- 수정
    updated_at TIMESTAMP NOT NULL, -- 수정

    CONSTRAINT fk_auto_integration_findex FOREIGN KEY (findex_id) REFERENCES findex (id) ON DELETE CASCADE
);

COMMENT ON TABLE auto_integration IS '자동 연동 설정';
COMMENT ON COLUMN auto_integration.id IS '자동연동설정ID (PK)';
COMMENT ON COLUMN auto_integration.findex_id IS '지수ID (FK)';
COMMENT ON COLUMN auto_integration.is_active IS '활성화 여부';
COMMENT ON COLUMN auto_integration.created_at IS '생성일시';
COMMENT ON COLUMN auto_integration.updated_at IS '수정일시';

COMMIT;
