package com.example.WIF.demoWIFPractice;

import com.azure.identity.WorkloadIdentityCredential;
import com.azure.identity.WorkloadIdentityCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.AccessPackageCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class DemoWifPracticeApplication {

	public static void main(String[] args)
	{
		final String clientId = System.getenv("AZURE_CLIENT_ID");
		final String tenantId = System.getenv("AZURE_TENANT_ID");
		final String tokenPath = System.getenv("AZURE_FEDERATED_TOKEN_FILE");

		if (clientId == null || tenantId == null || tokenPath == null) {
			System.err.println("Required env vars missing");
			System.exit(1);
		}

		WorkloadIdentityCredential cred = new WorkloadIdentityCredentialBuilder()
				.clientId(clientId)
				.tenantId(tenantId)
				.tokenFilePath(tokenPath)
				.build();

		TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(
				Collections.singletonList("https://graph.microsoft.com/.default"),
				cred
		);

		GraphServiceClient graph = GraphServiceClient.builder()
				.authenticationProvider(authProvider)
				.buildClient();

		AccessPackageCollectionPage page = graph
				.identityGovernance()
				.entitlementManagement()
				.accessPackages()
				.buildRequest()
				.get();

		System.out.println("Access Packages:");
		page.getCurrentPage().forEach(pkg ->
				System.out.printf("• %s (ID: %s)%n", pkg.displayName, pkg.id)
		);

	}

}
