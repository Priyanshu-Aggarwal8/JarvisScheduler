# 🧠 JarvisScheduler - Task Automation & Reminders

Welcome to **JarvisScheduler**! 🚀 This is an intelligent **task automation and reminder system** built using **Spring Boot, Quartz Scheduler, RabbitMQ, and HuggingFace API**. It helps users schedule tasks, get AI-generated task tips, and receive email reminders seamlessly. ✨

---

## 🌟 Features

✅ **Task Scheduling** – Create, update, and manage tasks effortlessly.\
✅ **AI-Powered Insights** – Get AI-generated task tips using HuggingFace.\
✅ **Email Reminders** – Receive timely email notifications for due tasks. 📩\
✅ **RabbitMQ Integration** – Reliable message-based task reminders. 🐇\
✅ **Natural Language Processing** – Enter dates in human-readable formats! ⏳\
✅ **Quartz Scheduler** – Robust and flexible scheduling system. ⏰

---

## 🏗️ Tech Stack

| Technology           | Purpose                |
| -------------------- | ---------------------- |
| **Spring Boot**      | Backend API            |
| **RabbitMQ**         | Asynchronous messaging |
| **HuggingFace API**  | AI-generated task tips |
| **MySQL**            | Database storage       |
| **Quartz Scheduler** | Task Scheduling        |
| **React (Planned)**  | User-friendly UI       |

---

## 🚀 Getting Started

### 🛠 Prerequisites

Ensure you have the following installed:

- Java 17+ ☕
- MySQL 📦
- RabbitMQ 🐇

### 🔧 Setup & Installation

1️⃣ **Clone the Repository**

```bash
 git clone https://github.com/Priyanshu-Aggarwal8/JarvisScheduler.git
 cd SmartScheduler
```

2️⃣ **Configure Database**\
Update `application.properties` with your MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smartscheduler
spring.datasource.username=root
spring.datasource.password=yourpassword
```

3️⃣ **Run RabbitMQ**\
Ensure RabbitMQ is running before launching the application.

```bash
rabbitmq-server start
```

4️⃣ **Build & Run the Application**

```bash
mvn clean install
mvn spring-boot:run
```

---

## 📌 API Endpoints

### 🔹 Task Management

| Method   | Endpoint              | Description              |
| -------- | --------------------- | ------------------------ |
| `POST`   | `/tasks/create`       | Create a new task        |
| `GET`    | `/tasks/user/{email}` | Get all tasks for a user |
| `PUT`    | `/tasks/update/{id}`  | Update a task            |
| `DELETE` | `/tasks/delete/{id}`  | Delete a task            |

### 🔹 AI-Powered Features

| Method | Endpoint                 | Description                               |
| ------ | ------------------------ | ----------------------------------------- |
| `POST` | `/tasks/naturalLanguage` | Create a task with NLP-based date parsing |

---

## ⏰ Quartz Scheduler Configuration

We are using **Quartz Scheduler** for scheduling task reminders efficiently.

✅ **Example: Task Reminder Job**

```java
@DisallowConcurrentExecution
@Component
public class TaskReminderJob implements Job {

    @Autowired
    private TaskService taskService;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // Fetch task and send reminder email
    }
}
```

---

## 🚀 Future Enhancements

✅ React-based frontend 🎨\
✅ Role-based access control 🔐\
✅ Task prioritization system 📊

---

## 🤝 Contributing

We welcome contributions! 🎉 To get started:

1. **Fork** the repository.
2. **Create a branch** for your feature (`git checkout -b feature-name`).
3. **Commit your changes** (`git commit -m 'Added a new feature'`).
4. **Push to GitHub** (`git push origin feature-name`).
5. **Create a Pull Request**. 🚀

---

## 📄 License

This project is licensed under the **MIT License**. 📜

---

## 📬 Contact

For queries, feel free to reach out! 📧\
👤 **Priyanshu Aggarwal**\
📧 Email: [agarwalpriyanshu812@gmail.com](mailto\:agarwalpriyanshu812@gmail.com)\
🔗 LinkedIn: https\://www\.linkedin.com/in/priyanshu-aggarwal-72063124a/

---

🌟 *If you find this project useful, don’t forget to ⭐ the repo!* 😃

