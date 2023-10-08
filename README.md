# spring reactive kotlin hazelcast cache 연동
kotlin을 사용하는 spring boot application에서 hazelcast 연동하는 예제

참고 자료
- https://github.com/hazelcast-demos/imperative-to-reactive

## 구조
밑의 두개의 프로젝트
- reactive
    - mono, flux를 사용한 예시    
- reactive-coroutines 
    - kotlin coroutines를 사용한 예시  

## 실행시 blockhound쪽 설정으로 인해 동작하지 않는 경우
밑의 옵션을 넣고 실행
```
-XX:+AllowRedefinitionToAddDeleteMethods
```
