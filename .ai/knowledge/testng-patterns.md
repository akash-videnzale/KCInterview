# TestNG patterns — how tests are organized

---

## Which base class?

| If your test… | Extend |
|---|---|
| Uses the browser | `UiBaseTest` |
| Calls APIs only | `ApiBaseTest` |

```java
public class SignupUiTest extends UiBaseTest {
    @Test(groups = {"ui", "smoke", "regression"})
    public void shouldRegisterUniqueUserAndDeleteAccount() { ... }
}
```

---

## Groups (labels for filtering)

| Group | Meaning |
|---|---|
| `ui` | Needs browser |
| `api` | API only |
| `integration` | Mix of layers |
| `smoke` | Fast critical checks |
| `regression` | Full set |
| `unit` | No live website needed |

Run only what you need:

```bash
mvn test -Dgroups=smoke
mvn test -Dgroups=ui
mvn test -Dgroups=api
```

---

## Assertions

Prefer helpers:

- `UiAssertions....`
- `ApiResponseAssertions....`
- `ProductAssertions....`

If you use SoftAssert, always finish with:

```java
softAssert.assertAll();
```

---

## Single vs parallel

**Default:** one test at a time (safer, easier to watch).

```bash
mvn test                              # single + visible browser
mvn test -DPARALLEL=true              # parallel on
mvn test -DPARALLEL=true -DTHREAD_COUNT=4
```

UI parallel is OK because each test has its own browser and a unique email.

---

## Naming

- Class: `LoginUiTest`, `ProductListApiTest`  
- Method: starts with `should...`  
- Example: `shouldLoginWithValidCredentials`  

---

## Cleanup

If you create a user, delete it.

```java
try {
    // test steps
} finally {
    userApi.deleteAccount(user.email(), user.password());
}
```

Browser cleanup is already done by `UiBaseTest`.
