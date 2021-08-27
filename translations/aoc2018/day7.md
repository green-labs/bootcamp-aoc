# Day 7

[https://adventofcode.com/2018/day/7](https://adventofcode.com/2018/day/7)

## 파트 1

스케줄이 주어질 때, 일이 처리되는 순서를 반환하시오.
알파벳 캐릭터 하나로 대표되는 일(work)이 주어지고, 각 일을 처리하기 위해서 선행되어야 하는 일들이 스케줄 형식으로 주어짐.
```
Step C must be finished before step A can begin.
Step C must be finished before step F can begin.
Step A must be finished before step B can begin.
Step A must be finished before step D can begin.
Step B must be finished before step E can begin.
Step D must be finished before step E can begin.
Step F must be finished before step E can begin.
```
위와 같은 입력의 경우 아래 형태의 그래프가 만들어짐.


```
  -->A--->B--
 /    \      \
C      -->D----->E
 \           /
  ---->F-----
```

순서는 아래와 같음.
- 처음엔 C만 가능함. C에서 시작. 만약 다른 시작점이 존재한다면 알파벳 순서로 진행.
- C 다음으로 A와 F가 가능한데, A가 알파벳 우선순위가 높으므로 A로 감.
- A 를 완료하면 B, D, F가 가능한데, 역시 알파벳 우선순위가 높은 B가 수행됨.
- 남은 D와 F중에서 D가 수행됨
- F가 수행됨
- E가 수행됨 (E는 B, D, F 모두가 선행되어야 수행될 수 있음)

결과: `CABDFE`


## 파트 2

파트 1에서는 일을 워커(worker)가 하나였지만, 파트 2는 5명. 즉, 동시에 5개의 일을 처리할 수 있게 됨.
그리고 각각의 일 (A\~Z)은 처리하는데 (60+1\~60+26)의 시간이 걸림. B는 62초, D는 64초, etc.

이 때, 주어진 모든 일을 처리하는데 걸리는 시간을 구하시오.

예)

아래는 파트 1의 예시에서 워커가 2명이 된 케이스이다.
```
Second   Worker 1   Worker 2   Done
   0        C          .        
   1        C          .        
   2        C          .        
   3        A          F       C
   4        B          F       CA
   5        B          F       CA
   6        D          F       CAB
   7        D          F       CAB
   8        D          F       CAB
   9        D          .       CABF
  10        E          .       CABFD
  11        E          .       CABFD
  12        E          .       CABFD
  13        E          .       CABFD
  14        E          .       CABFD
  15        .          .       CABFDE
```
15초가 걸리므로 답은 15