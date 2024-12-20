### 요구사항 기본과제 

- 포인트 충전, 사용에 대한 정책 추가 (잔고 부족, 최대 잔고 등) 
  최대 잔고는 1000
  잔고가 부족하면 exception
- 동시에 여러 요청이 들어오더라도 순서대로 (혹은 한번에 하나의 요청씩만) 제어될 수 있도록 리팩토링

- 동시성 제어에 대한 통합 테스트 작성

### 심화과제 

동시성 제어 방식에 대한 분석 및 보고서 작성


#### 동시성 제어 ?
다수의 프로세스나 스레드가 동시에 동일한 자원에 접근 할 때 발생하는 충돌이나 일관성 문제를 방지하고 관리하는 기술. 

주요이슈 
- race condition
  여러 프로세스나 스레드가 동시에 공유 자원에 접근하여 작업을 수행할 때, 실행 순서에 따라 결과가 달라질 수 있는 상황.
- dead lock
  두 개 이상의 프로세스가 서로가 소유한 자원을 기다리며 무한히 대기하는 상태.
- inconsistencies
  데이터가 예상과 다르게 변경되어 데이터베이스나 시스템의 일관성이 깨지는 상황.
- phantom reads
  트랜잭션이 동일한 쿼리를 여러 번 수행할 때, 다른 트랜잭션에 의해 추가된 행을 읽는 현상.

이중 race condition이 test 가능하면서 과제에서 풀어내야할 issue로 생각하였습니다. 

### 해결 방법. 

처음 과제를 접했을때는 큐를 활용해서 순서를 보장하고 처리하도록 구현하면 되지 않을까 ? 라고 생각했습니다. - x 
이후 멘토링을 통해 lock 으로 해결하는 것이 보편적이라는 코멘트를 받았고 lock 기능을 구현하는 쪽으로 방향성을 잡았습니다. 

ReentrantLock을 활용하여 로직을 작성하였습니다. 


ReentrantLock을 적용하게 된 이유는 다음과 같습니다. 
- 동시성 이슈를 제어하고 성능도 보장할 수 있으려면 lock이 걸리는 임계지점을 최대한 짧게 가져가야 된다고 생각했고,
짧게 가져갔을때 락 획득 및 해제가 용이하고 시간제한이 있어서 데드락을 방지할 수 있는 ReentrantLock 을 사용해야겠다 판단하였습니다.

\\\
    private val lock = ReentrantLock()

    override fun useUserPointById(id: Long, amount: Long): UserPoint {

        return lock.withLock {
            useUserPoint(id, amount)
        }

    }

\\\

위처럼 확장함수를 사용하여 블록이 종료되면 자동으로 락이 해제되도록 작성하였습니다. 