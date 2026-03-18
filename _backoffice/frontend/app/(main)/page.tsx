'use client';

import { Card } from '../../components';
import styles from './page.module.css';

export default function Home() {
  const stats = [
    { label: '전체 사용자', value: '12,456명', trend: '+12.5%', isUp: true },
    { label: '금일 가입자', value: '156명', trend: '+5.2%', isUp: true },
    { label: '활성 세션', value: '892개', trend: '-2.1%', isUp: false },
    { label: '일간 매출액', value: '₩1,245,000', trend: '+8.7%', isUp: true },
  ];

  return (
    <div className={styles.dashboard}>
      <header className={styles.welcome}>
        <h1 className={styles.title}>관리자 대시보드</h1>
        <p className={styles.subtitle}>오늘의 주요 지표와 시스템 상태를 확인하세요.</p>
      </header>

      <div className={styles.statsGrid}>
        {stats.map((stat, index) => (
          <Card key={index} className={styles.statCard}>
            <span className={styles.statLabel}>{stat.label}</span>
            <span className={styles.statValue}>{stat.value}</span>
            <div className={`${styles.statTrend} ${stat.isUp ? styles.up : styles.down}`}>
              <span>{stat.isUp ? '▲' : '▼'}</span>
              <span>{stat.trend}</span>
              <span style={{ color: '#666', marginLeft: '4px' }}>지난주 대비</span>
            </div>
          </Card>
        ))}
      </div>

      <div style={{ marginTop: '2.5rem' }}>
        <Card style={{ padding: '2rem', textAlign: 'left' }}>
          <h3 style={{ marginBottom: '1rem' }}>최근 시스템 알림</h3>
          <ul style={{ listStyle: 'none', padding: 0 }}>
            <li style={{ padding: '10px 0', borderBottom: '1px solid #eee' }}>
              <span style={{ color: '#007bff', fontWeight: 'bold', marginRight: '10px' }}>[공지]</span>
              시스템 점검이 3월 20일 오전 2시에 예정되어 있습니다.
            </li>
            <li style={{ padding: '10px 0', borderBottom: '1px solid #eee' }}>
              <span style={{ color: '#28a745', fontWeight: 'bold', marginRight: '10px' }}>[성공]</span>
              새로운 데이터 백업이 성공적으로 완료되었습니다.
            </li>
            <li style={{ padding: '10px 0' }}>
              <span style={{ color: '#d93025', fontWeight: 'bold', marginRight: '10px' }}>[경고]</span>
              비정상적인 로그인 시도가 3회 감지되었습니다.
            </li>
          </ul>
        </Card>
      </div>
    </div>
  );
}
