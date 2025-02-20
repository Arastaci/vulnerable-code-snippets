// $ ls -lh /opt/logger/bin/
// -rwsrwsr-x 1 root root  14K Dec 11 13:37 loggerctl

char *logger_path, *cmd;

void rotate_log() {
    char log_old[PATH_MAX], log_new[PATH_MAX], timestamp[0x100];
    time_t t;
    time(&t);
    strftime(timestamp, sizeof(timestamp), "%FT%T", gmtime(&t));
    snprintf(log_old, sizeof(log_old), "%s/../logs/global.log", logger_path);
    snprintf(log_new, sizeof(log_new), "%s/../logs/global-%s.log", logger_path, timestamp);
    execl("/bin/cp", "/bin/cp", "-a", "--", log_old, log_new, NULL);
}

int main(int argc, char **argv) {
    if (argc != 2) {
        printf("Usage: /opt/logger/bin/loggerctl <cmd>\n");
        return 1;
    }

    if (setuid(0) == -1) return 1;
    if (seteuid(0) == -1) return 1;

    char *executable_path = argv[0];
    logger_path = dirname(executable_path);
    cmd = argv[1];

    if (!strcmp(cmd, "rotate")) rotate_log();
    else list_commands();
    return 0;
}