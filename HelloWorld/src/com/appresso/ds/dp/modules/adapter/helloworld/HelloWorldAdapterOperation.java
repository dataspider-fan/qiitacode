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

		
		/*
		 * スマートコントラクトの関数呼び出し（ローカル環境の参照系呼び出し）
		 */
		// Private network wrapperの生成
		// ApiWrapper( gRPC FullNode, gRPC SolidityNode, private key)
		// Docker起動時の50051と50052ポートを指定
		ApiWrapper wrapper_local =  new ApiWrapper("127.0.0.1:50051", "127.0.0.1:50052", "");

		// MyTokenをデプロイしたアドレスの秘密鍵をKeyPairメソッドに渡す
		KeyPair keyPair_local = new KeyPair("e26d5a9e94aea8e35b6b2668c298d01fe09c2490c47d242eb4cfd036188f0b51");

		// MyTokenの残高（balanceOf）を取得するアドレスを変数に格納する
		String ownerAddr_local = keyPair_local.toBase58CheckAddress();

		// 上記、MyTokenのデプロイ後のスマートコントラクトアドレスを指定する
		String cntrAddr_local = "TTq6PiZee7XHiMUdcY5s4UXSrdXrHnLVbw";

		// getBalance関数の呼び出し
		Function balanceOf_local = new Function("balanceOf",
		Arrays.asList(new Address(ownerAddr_local)), Arrays.asList(new TypeReference<Uint256>() {}));

		// getBalanceの取得結果を実行ログに表示
		TransactionExtention txnExt_local = wrapper_local.constantCall(ownerAddr_local, cntrAddr_local, balanceOf_local);
		String result_local = Numeric.toHexString(txnExt_local.getConstantResult(0).toByteArray());
		log.info("balanceOf : " + FunctionReturnDecoder.decode(result_local, balanceOf_local.getOutputParameters()).get(0).getValue());

		
		/*
		 * スマートコントラクトの関数呼び出し（更新系）
		 */
		// Function transferに渡すパラメータ
		long amount = 1; 		// 送金額
		int powerValue = 2; 		// TODO Calculation methods and estimation logic need to be implemented separately.
		long limitForFee = 5000000; 	// TODO Calculation methods and estimation logic need to be implemented separately.
		String TransactionMessage = "transferCallForLocal";

		// MyTokenコントラクトアドレス
		String cntrAddrModify = "TTq6PiZee7XHiMUdcY5s4UXSrdXrHnLVbw";

		// MyTokenコントラクトデプロイアカウントの秘密鍵
		String privateModifyKey = "e26d5a9e94aea8e35b6b2668c298d01fe09c2490c47d242eb4cfd036188f0b51";

		// localのgRPCサービスに接続
		ApiWrapper wrapperModify =  new ApiWrapper("127.0.0.1:50051", "127.0.0.1:50052", privateModifyKey);

		// 送金元アドレス　TPPDEF8x6Pe3R16dJJi7Ui8fyjg1k1yUpo 
		KeyPair keyPairModify = new KeyPair(privateModifyKey);
		String ownerAddrModify = keyPairModify.toBase58CheckAddress();
		// 送金先アドレス
		String destAddr = "TJGD2HHUz2poYebAaCLNxNQE1ZCEJ2nfuu";

		// transferスマートコントラクト呼び出し
		Function transfer = new Function("transfer",
						Arrays.asList(new Address(destAddr),
						new Uint256(BigInteger.valueOf(amount).multiply(BigInteger.valueOf(10).pow(powerValue)))),
						Arrays.asList(new TypeReference<Bool>() {}));

		TransactionBuilder Trxbuilder = wrapperModify.triggerCall(ownerAddrModify, cntrAddrModify, transfer);
		Trxbuilder.setFeeLimit(limitForFee);
		Trxbuilder.setMemo(TransactionMessage);


		/*
		 * 送金先アドレスの残高（送金送金前）
		*/
		// getBalance関数の呼び出し
		balanceOf_local = new Function("balanceOf",
					Arrays.asList(new Address(destAddr)), Arrays.asList(new TypeReference<Uint256>() {}));

		// getBalanceの取得結果を実行ログに表示
		txnExt_local = wrapper_local.constantCall(destAddr, cntrAddrModify, balanceOf_local);
		result_local = Numeric.toHexString(txnExt_local.getConstantResult(0).toByteArray());
		log.info("送信先 送金前 残高 : " + FunctionReturnDecoder.decode(result_local, balanceOf_local.getOutputParameters()).get(0).getValue());

		 /*
		 * 送金実行
		 */
		log.info("送金実行");
		Transaction toSendTxn = wrapperModify.signTransaction(Trxbuilder.build());
		String toSendResult = wrapperModify.broadcastTransaction(toSendTxn);
		log.info("transactionHash : " + toSendResult);

		/*
		 * 送金先アドレスの残高（送金後）
		 */
		// getBalance関数の呼び出し
		balanceOf_local = new Function("balanceOf",
		Arrays.asList(new Address(destAddr)), Arrays.asList(new TypeReference<Uint256>() {}));

		// getBalanceの取得結果を実行ログに表示
		txnExt_local = wrapper_local.constantCall(destAddr, cntrAddrModify, balanceOf_local);
		result_local = Numeric.toHexString(txnExt_local.getConstantResult(0).toByteArray());
		log.info("送信先 送金後 残高 : " + FunctionReturnDecoder.decode(result_local, balanceOf_local.getOutputParameters()).get(0).getValue());
		
		return new HashMap();
	}

	@Override
	public void destroy() {
	}
}
