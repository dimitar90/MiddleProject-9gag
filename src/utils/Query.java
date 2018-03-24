package utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import annotations.Column;

public class Query {
	private static Query query;

	private Query() {
	}

	public static Query getInstance() {
		if (query == null) {
			query = new Query();
		}

		return query;
	}

	// vzima poletata na dadeno entity koito sa s annotaciq "Column" s reflection i
	// pravi insert zaqvka kum bazata
	// na koqto trqbva da se podadat stoinostite kato argumenti
	public <E> String getInsertQuery(E entity) {
		StringBuilder sb = new StringBuilder();

		String tableName = (entity.getClass().getSimpleName() + "s").toLowerCase();

		List<String> fieldNames = Arrays.stream(entity.getClass().getDeclaredFields())
				.filter(f -> f.getDeclaredAnnotation(Column.class) != null)
				.map(f -> f.getDeclaredAnnotation(Column.class).name()).collect(Collectors.toList());

		StringBuilder params = new StringBuilder();
		for (int index = 0; index < fieldNames.size(); index++) {
			if (index == fieldNames.size() - 1) {
				params.append("?");
			} else {
				params.append("?, ");
			}

		}

		sb.append("INSERT INTO ").append(tableName).append(" (").append(String.join(", ", fieldNames)).append(")")
				.append(" VALUES (").append(params.toString()).append(")");

		return sb.toString();
	}
}
