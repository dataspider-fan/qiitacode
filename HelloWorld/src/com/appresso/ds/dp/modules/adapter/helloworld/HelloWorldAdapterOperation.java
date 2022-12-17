package com.appresso.ds.dp.modules.adapter.helloworld;

import java.util.HashMap;
import java.util.Map;

import org.tron.trident.core.key.KeyPair;

import com.appresso.ds.common.fw.LoggingContext;
import com.appresso.ds.dp.spi.Operation;
import com.appresso.ds.dp.spi.OperationContext;

public class HelloWorldAdapterOperation implements Operation {
	private final OperationContext context;

	public HelloWorldAdapterOperation(OperationContext context) {
		this.context = context;
	}

	@Override
	public Map execute(Map inputData) throws Exception {

		// アドレスを生成
		KeyPair keyPairGenarate = KeyPair.generate();
		String publicAddress =keyPairGenarate.toBase58CheckAddress();
		String privateKey = keyPairGenarate.toPrivateKey();

		LoggingContext log = context.log();
		log.finest("*******************");
		log.info("** Tron address **");
		// 実行ログに生成したアドレスを表示する
		log.info("PUBLIC_ADDRESS : " + publicAddress);
		log.info("PTIVATE_KEY : " + privateKey);
		log.finest("*******************");

		return new HashMap();
	}

	@Override
	public void destroy() {
	}
}