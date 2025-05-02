import { useEvent } from "expo";
import { PropsWithChildren, useEffect, useState } from "react";
import {
	Button,
	Platform,
	SafeAreaView,
	ScrollView,
	Text,
	View,
} from "react-native";
import SecurityChecker, { type SecurityCheckReport } from "security-checker";

const Evidence = ({ evidence }: { evidence: Record<string, string[]> }) => {
	if (!evidence) return null;
	return (
		<>
			{Object.entries(evidence)?.map(
				([key, value]) =>
					!!value && (
						<View key={key}>
							<Text style={{ color: "white", fontSize: 16 }}>{key}:</Text>
							{value?.map((item, index) => (
								<Text key={`${key}-${item}`} style={{ color: "white" }}>
									{index + 1}) {item}
								</Text>
							))}
						</View>
					),
			)}
		</>
	);
};

const RenderData = ({ report }: { report: SecurityCheckReport }) => {
	return (
		<View>
			<View
				style={{
					backgroundColor: report.isSecure ? "green" : "red",
					padding: 10,
					borderRadius: 10,
				}}
			>
				<Text style={{ color: "white", fontSize: 20 }}>
					{report.isSecure ? "Secure" : "Environment is not secure"}
				</Text>
			</View>

			<View
				style={{
					backgroundColor:
						report.debugger?.value === true
							? "red"
							: report.debugger?.value === false
								? "green"
								: "yellow",
					padding: 10,
					marginTop: 10,
					borderRadius: 10,
				}}
			>
				<Text style={{ color: "white", fontSize: 18 }}>
					{report.debugger?.value === true
						? "Debugger detected"
						: report.debugger?.value === false
							? "Debugger is not detected"
							: "Could not determine"}
				</Text>
				<Evidence evidence={report.debugger?.evidence} />
			</View>

			<View
				style={{
					backgroundColor: report.emulator?.value ? "red" : "green",
					padding: 10,
					marginTop: 10,
					borderRadius: 10,
				}}
			>
				<Text style={{ color: "white", fontSize: 18 }}>
					{report?.emulator?.value ? "Run on simulator" : "Run on real device"}
				</Text>
				<Evidence evidence={report.emulator?.evidence} />
			</View>

			{Platform.OS === "ios" && (
				<RenderIOSData report={report as SecurityCheckReport<"ios">} />
			)}

			{Platform.OS === "android" && (
				<RenderAndroidData report={report as SecurityCheckReport<"android">} />
			)}
		</View>
	);
};

const RenderIOSData = ({ report }: { report?: SecurityCheckReport<"ios"> }) => {
	if (!report) return null;

	return (
		<View
			style={{
				backgroundColor: report.jailbreak?.value ? "red" : "green",
				padding: 10,
				marginTop: 10,
				borderRadius: 10,
			}}
		>
			<Text style={{ color: "white", fontSize: 18 }}>
				{report?.jailbreak?.value
					? "Jailbreak detected"
					: "Device is not jailbroken"}
			</Text>
			<Evidence evidence={report.jailbreak?.evidence} />
		</View>
	);
};

const RenderAndroidData = ({
	report,
}: { report?: SecurityCheckReport<"android"> }) => {
	if (!report) return null;

	return (
		<View
			style={{
				backgroundColor: report.root?.value ? "red" : "green",
				padding: 10,
				marginTop: 10,
				borderRadius: 10,
			}}
		>
			<Text style={{ color: "white", fontSize: 18 }}>
				{report?.root?.value ? "Root detected" : "Device is not rooted"}
			</Text>
			<Evidence evidence={report.root?.evidence} />
		</View>
	);
};

export default function App() {
	const [data, setData] = useState<SecurityCheckReport>();

	useEffect(() => {
		const setReport = (report: SecurityCheckReport) => {
			console.log(report);
			setData(report);
		};
		const res = SecurityChecker.check();
		setReport(res);
		SecurityChecker.addListener("onChange", setReport);

		return () => {
			SecurityChecker.removeListener("onChange", setReport);
		};
	}, []);

	return (
		<SafeAreaView style={styles.container}>
			<ScrollView style={styles.container}>
				<Text style={styles.header}>Module API Example</Text>
				<Group name="Functions">{!!data && <RenderData report={data} />}</Group>
			</ScrollView>
		</SafeAreaView>
	);
}

function Group(props: { name: string; children: React.ReactNode }) {
	return (
		<View style={styles.group}>
			<Text style={styles.groupHeader}>{props.name}</Text>
			{props.children}
		</View>
	);
}

const styles = {
	header: {
		fontSize: 30,
		margin: 20,
	},
	groupHeader: {
		fontSize: 20,
		marginBottom: 20,
	},
	group: {
		margin: 20,
		backgroundColor: "#fff",
		borderRadius: 10,
		padding: 20,
	},
	container: {
		flex: 1,
		backgroundColor: "#eee",
	},
	view: {
		flex: 1,
		height: 200,
	},
};
