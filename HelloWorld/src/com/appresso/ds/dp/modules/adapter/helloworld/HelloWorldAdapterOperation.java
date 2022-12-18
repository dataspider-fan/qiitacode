package com.appresso.ds.dp.modules.adapter.helloworld;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.tron.trident.abi.FunctionReturnDecoder;
import org.tron.trident.abi.TypeReference;
import org.tron.trident.abi.datatypes.Address;
import org.tron.trident.abi.datatypes.Function;
import org.tron.trident.abi.datatypes.generated.Uint256;
import org.tron.trident.core.ApiWrapper;
import org.tron.trident.core.contract.Contract;
import org.tron.trident.core.key.KeyPair;
import org.tron.trident.proto.Response.TransactionExtention;
import org.tron.trident.utils.Numeric;

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

		/*
		 * TRONアドレス生成
		 */
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


		/*
		 * スマートコントラクトの関数呼び出し（参照系）
		 */
		// NILE testnet wrapperの生成
		// アドレスの秘密鍵をofNileメソッドの引数に渡す
		ApiWrapper wrapper = ApiWrapper.ofNile("08b284c0e6122da673cdd9b5599699aabe5e6353412c1c72f37189a5790a4f86"); ///ofShasta()/ofNile()

		// MetaCoinをデプロイしたアドレスの秘密鍵をKeyPairメソッドに渡す
		KeyPair keyPair = new KeyPair("b59e5dfbd74a50122277340d4d8642bc78370da396da4bf9b33757cfa6b4bece");

		// MetaCoinの残高（balanceOf）を取得するアドレスを変数に格納する
		String ownerAddr = keyPair.toBase58CheckAddress();

		// 上記、MetaCoinのデプロイ後のスマートコントラクトアドレスを指定する
		String cntrAddr = "TTq6PiZee7XHiMUdcY5s4UXSrdXrHnLVbw";

		// getBalance関数の呼び出し
		Function balanceOf = new Function("getBalance",
		Arrays.asList(new Address(ownerAddr)), Arrays.asList(new TypeReference<Uint256>() {}));

		// getBalanceの取得結果を実行ログに表示
		TransactionExtention txnExt = wrapper.constantCall(ownerAddr, cntrAddr, balanceOf);
		String result = Numeric.toHexString(txnExt.getConstantResult(0).toByteArray());
		log.info("getBalance : " + FunctionReturnDecoder.decode(result, balanceOf.getOutputParameters()).get(0).getValue());

		return new HashMap();
	}

	@Override
	public void destroy() {
	}
}
