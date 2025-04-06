package backend.models;

public class User {
    private int studentId;
    private String username;
    private String password;
    private String email;

    public User() {}

    public User(int studentId, String username, String password, String email) {
        this.studentId = studentId;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
               "studentId=" + studentId +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' + '}';
    }
} 