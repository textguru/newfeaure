package org.egovframe.rte.psl.dataaccess;

import java.util.List;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;

/**
 * eGovFrame RTE {@code EgovAbstractMapper} 호환 경량 베이스 클래스.
 *
 * <p>이 샌드박스에서는 eGovFrame Maven 저장소 접근이 차단되어 정식 RTE jar를
 * 받을 수 없으므로, 빌드/실행 검증을 위해 동일 패키지/클래스명으로 MyBatis
 * SqlSessionTemplate 위임만 제공한다. 정식 RTE의 selectList/selectOne/insert
 * /update/delete 시그니처를 그대로 따른다. 실제 eGovFrame 환경에서는 이 파일을
 * 삭제하고 {@code org.egovframe.rte.psl.dataaccess} jar를 사용한다.</p>
 */
public abstract class EgovAbstractMapper {

    private SqlSessionTemplate sqlSession;

    @Resource(name = "sqlSession")
    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    public <E> List<E> selectList(String queryId) {
        return sqlSession.selectList(queryId);
    }

    public <E> List<E> selectList(String queryId, Object parameterObject) {
        return sqlSession.selectList(queryId, parameterObject);
    }

    public Object selectOne(String queryId) {
        return sqlSession.selectOne(queryId);
    }

    public Object selectOne(String queryId, Object parameterObject) {
        return sqlSession.selectOne(queryId, parameterObject);
    }

    public int insert(String queryId, Object parameterObject) {
        return sqlSession.insert(queryId, parameterObject);
    }

    public int update(String queryId, Object parameterObject) {
        return sqlSession.update(queryId, parameterObject);
    }

    public int delete(String queryId, Object parameterObject) {
        return sqlSession.delete(queryId, parameterObject);
    }
}
