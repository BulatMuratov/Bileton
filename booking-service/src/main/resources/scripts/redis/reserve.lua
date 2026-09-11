for _, key in ipairs(KEYS) do
	if redis.call('EXISTS', key) == 1 then
		return 0
	end
end

for _, key in ipairs(KEYS) do
	redis.call('SET', key, ARGV[1], 'EX', ARGV[2])
end

return 1