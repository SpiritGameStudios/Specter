package dev.spiritstudios.specter.api.serialization;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.List;

/**
 * A codec with additional comments.
 * These comments are added to the serialized object if it implements {@link Commentable}.
 */
public class CommentedCodec<T> implements Codec<T> {
	protected final Codec<T> codec;
	protected final List<String> comments;

	public CommentedCodec(Codec<T> codec, List<String> comments) {
		this.codec = codec;
		this.comments = comments;
	}

	public CommentedCodec(Codec<T> codec, String... comments) {
		this(codec, List.of(comments));
	}

	@Override
	public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
		return codec.decode(ops, input);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
		DataResult<T1> result = codec.encode(input, ops, prefix);
		if (!result.hasResultOrPartial() || !(result.getPartialOrThrow() instanceof Commentable commentable))
			return result;

		commentable.setComments(comments.toArray(String[]::new));
		return DataResult.success((T1) commentable);

	}
}
