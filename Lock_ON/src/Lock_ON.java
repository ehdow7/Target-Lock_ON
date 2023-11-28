import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Lock_ON extends JFrame {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int GREEN_DOT_SIZE = 15;
    private static final int RED_DOT_SIZE = 5;
    private static final int MAX_DOTS = 8;

    private int score = 0;
    private int count = 0;
    private int lives = 5;
    private int restart = 0;
    
    private String name;
    
    Timer timer = new Timer(1250 - this.score * 10, e -> this.createDot()); //점수에 비례해 속도 빨라지며 createDot함수 호출하는 타이머 생성

    private Random random = new Random();//Random 클래스 객체 생성

    private List<Point> dotPositions = new ArrayList<>(); //dotPositions 리스트 생성, Point 객체 보관 -> 초록점 위치 추적
    private boolean gameStarted = false; //boolean 변수 게임 시작 확인
    private Dot redDot; // 빨간 점 추적 변수

    public Lock_ON() {
    	
        setTitle("Target: Lock_ON - 점수 : 0 생명 : 5"); //Title 변경
        setSize(WIDTH, HEIGHT); //창 사이즈 변경
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //프레임 닫을 때 프로그램 종료
        setLocationRelativeTo(null);//프레임 위치 중앙 설정
        getContentPane().setBackground(Color.BLACK); // 배경색을 검은색으로 변경
        setLayout(null); // Use absolute layout

        setVisible(true); //프레임 보이게

        JButton startButton = new JButton("시작"); //시작 버튼
        startButton.setBounds(WIDTH / 2 - 50, HEIGHT / 2 +50, 100, 50); //버튼 위치, 크기 설정
        startButton.addActionListener(e -> inputName()); //클릭하면 startGame함수
        add(startButton); //화면에 나타나게 만들기
        
        JButton infoButton = new JButton("게임 방법"); //게임 방법 버튼
        infoButton.setBounds(WIDTH / 2 - 50, HEIGHT / 2 + 175, 100, 50);//버튼 위치, 크기 설정
        infoButton.addActionListener(e -> showGameInfo()); // 클릭하면 showGameInfo함수
        add(infoButton);
        
        JLabel titleLabel = new JLabel("Target: Lock_ON"); //제목
        titleLabel.setFont(new Font("Arial", Font.BOLD, 50)); //폰트,크기 설정
        titleLabel.setForeground(Color.WHITE); // 텍스트 색상 설정
        titleLabel.setBounds(WIDTH / 2 - 210, HEIGHT / 2 -650, 2000, 1000); //버튼 위치, 크기 설정
        add(titleLabel);


        // 마우스 클릭 이벤트 감지 리스너 추가
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (gameStarted) createClickDot(e.getX(), e.getY()); // 게임이 시작되었을 때만 클릭된 점 생성
            }
        });
    }
    
    private void inputName() {
    	name = JOptionPane.showInputDialog("닉네임을 입력하세요");
    	startGame();
    }

    //게임 시작
    private void startGame() {
        this.gameStarted = true; //boolean 변수 true
        this.getContentPane().removeAll(); //화면 초기화
        this.repaint(); //화면 갱신
        timer.start(); //타이머 시작
    }


    private void showGameInfo() {
        JOptionPane.showMessageDialog(this,	
                "Target: Lock_ON\n" +
                        "게임이 시작되면 랜덤한 위치에 초록 점이 나타납니다.\n" +
                        "초록 점을 눌러서 점수를 획득하세요. 생성 주기는 점점 짧아집니다. 기본 생명은 5입니다.\n" +
                        "초록 점이 8개 이상 존재하거나, 잘못 클릭하여 생명이 0이 되면 게임이 종료됩니다.\n"+
                        "주의점1 - 터치하여 생기는 빨간 점이 초록점안에 70%정도 들어가야 인식이 됩니다.\n"+
                        "주의점2 - 터치를 여러번하면 인식이 여러번 들어와 게임이 종료될 수 있습니다.\n"+
                        "주의점3 - 터치가 씹히는 경우가 종종 있습니다. 빨간 점이 생겼는지 확인 후 다시 눌러주세요.\n"); //게임 방법 보여주기
    }
    
    //초록점 만들기
    private void createDot() {
        if (this.count >= 8) {
            this.saveData();
        }
 
        Point newDot = new Point(random.nextInt(WIDTH - GREEN_DOT_SIZE*2), random.nextInt(HEIGHT - GREEN_DOT_SIZE*2-40)); //프레임 안에 랜덤한 위치를 가지는 초록 점 좌표 생성
        dotPositions.add(newDot); //리스트에 좌표 추가
        Dot dot = new Dot(newDot.x, newDot.y, Color.GREEN, GREEN_DOT_SIZE); // 초록 점 생성
        add(dot);//화면에 추가
        this.revalidate();
        this.repaint();
        count++;
    }

    //빨간점 생성
    private void createClickDot(int x, int y) {
        // 이전에 생성된 빨간 점이 있다면 제거
        if (redDot != null) {
            remove(redDot);
        }

        x -= 10; // x 좌표 조정
        y -= 35; // y 좌표 조정

        // 새로운 빨간 점 생성
        redDot = new Dot(x - RED_DOT_SIZE / 2, y - RED_DOT_SIZE / 2, Color.RED, RED_DOT_SIZE);
        add(redDot);

        boolean overlap = false; // 겹침 여부 확인 변수
        
        // 초록점 빨간점 겹침 확인
        for (Point point : dotPositions) { //리스트 속 객체 순차적으로 가져오기
            if (point.getX() <= x && x <= point.getX() + GREEN_DOT_SIZE && //만약에 겹치면
                    point.getY() <= y && y <= point.getY() + GREEN_DOT_SIZE) {
                score++;
                count--;
                dotPositions.remove(point); //좌표 객체 삭제
                removeDotAt(point.x, point.y); // 초록점 삭제
                setTitle("Target: Lock_ON - 점수 : " + score + " 생명 : " + lives); //제목 정보 변경
                overlap = true; // 겹침 확인
                break; //종료
            }
        }

        // 겹침이 없으면
        if (!overlap) {
            lives--;
            setTitle("Target: Lock_ON - 점수 : " + score + " 생명 : " + lives); //제목 정보 변경
            if (lives == 0) {
                saveData(); // 게임 종료 함수
            }
        }

        this.revalidate();
        this.repaint();
    }

    //초록점 삭제
    private void removeDotAt(int x, int y) { 
        Component[] greenDots = this.getContentPane().getComponents(); // 모든 초록점 배열로 변환
        for (Component dot : greenDots) {
            if (dot instanceof Dot && dot.getX() == x && dot.getY() == y) { //점이 해당 좌표에 있으면
                this.remove(dot); //초록점 삭제
                break;
            }
        }
    }

    private class Dot extends JPanel {
        private Color dotColor;
        private int dotSize;

        public Dot(int x, int y, Color color, int size) {
            setBounds(x, y, size, size);
            dotColor = color;
            dotSize = size;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(dotColor);
            g.fillRect(0, 0, dotSize, dotSize);
        }
    }
    
    private void saveData() {
    	timer.stop();
    	        try {
    	        	
    	    //경로 변경하기
            File file = new File("C:\\Users\\user\\Downloads\\Lock_ON_FinalTest\\Lock_ON\\gameData.txt"); //파일 객체 생성
 
            if (!file.exists()) { //파일 존재여부 체크
                file.createNewFile();//파일 생성
            }
 
            FileWriter fw = new FileWriter(file,true);//Writer 생성
            BufferedWriter writer = new BufferedWriter(fw);
 
            writer.write("닉네임 : "+name+"\n점수 : "+score+"\n재시작 횟수 : "+restart+"\n\n"); //정보 파일에 저장

            writer.close();//Writer 종료
 
        } catch (IOException e) {
            e.printStackTrace();
        }
    	endGame();
    
    }
    
    private void endGame() {
        int ox = JOptionPane.showConfirmDialog(this, "게임 종료! \n닉네임 : "+name+" 점수 : " + this.score+"\n재시작 하겠습니까?", "confirm",JOptionPane.YES_NO_OPTION );
        if(ox==JOptionPane.YES_OPTION){  //YES면
			score=0; //재시작 변수 세팅
			lives=5;
			count=0;
			restart++;
			List<Point> dotPositions = new ArrayList<>(); //리스트 초기화
			gameStarted = false;
			setTitle("Target: Lock_ON - 점수 : 0 생명 : 5");
			startGame();
		} else{  //No 라면
			System.exit(0); //종료
		}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Lock_ON()); 
        // SwingUtilities.invokeLater 스레드에서 코드를 실행시키는 메서드
        // Lock_ON 클래스 실행
    }
}



