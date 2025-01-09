package co.casterlabs.tcp_proxy;

import co.casterlabs.rakurai.json.annotating.JsonClass;
import co.casterlabs.rakurai.json.validation.JsonValidate;

@JsonClass(exposeAll = true)
public class Config {
    public String bindAddress = "::";
    public int bindPort = 8080;

    public String targetAddress = "example.com";
    public int targetPort = 80;

    public int soTimeoutSeconds = 30;

    @JsonValidate
    private void $validate() {
        if (this.bindPort < 0 || this.bindPort > 65535) throw new IllegalArgumentException("bindPort must be between 0 and 65535 (inclusive).");
        if (this.targetPort < 0 || this.targetPort > 65535) throw new IllegalArgumentException("targetPort must be between 0 and 65535 (inclusive).");
        if (this.soTimeoutSeconds < 0) throw new IllegalArgumentException("soTimeoutSeconds must be greater than or equal to 0.");
        if (this.bindAddress == null) throw new IllegalArgumentException("bindAddresses item cannot be null.");
        if (this.targetAddress == null) throw new IllegalArgumentException("targetAddress cannot be null.");
    }

}
