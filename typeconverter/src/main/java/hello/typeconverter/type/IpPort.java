package hello.typeconverter.type;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
// HashCode: 객체의 필드값들을 특정 공식에 넣어 정수(해시코드)로 변환, 이 계산은 딱 한번만 수행하면 되고, 숫자끼리의 비교 연산 속도는 매우 빠름
/*
    equals와 hashCode를 동시에 선언해야 하는 이유는 객체의 모든 필드를 일일이 대조하는 equals()는 연산 비용이 높음 이를 보안하기 위해
    hashCode를 사용하여 빠르게 객체의 필드값들으로 계산이 된 숫자를 비교를 하여 두 객체의 필드 값이 같은지를 확인할 수 있게 됨

    만약 equals만 정의하고 hashCode를 그대로 두면, 값이 같은 객체라도 해시코드가 달라져서 equals()의 결과가 모순이 되어 나올 수가 있게 된다.
    왜냐하면 자바의 최상위 클래스인 Object가 기본적으로 제공하는 hashCode()는 객체의 메모리 주소값을 기반으로 해시코드를 만들기 때문에
    아무리 필드 값이 똑같아도, 인스턴스를 새로 생성할 때마다 자바 입장에서는 새로운 주소를 할당 받은 객체이므로 전혀 다른 해시코드를 부여하게 됨
*/
@EqualsAndHashCode
public class IpPort {

    private String ip;
    private int port;

    public IpPort(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
}
