-- apps_user
insert into `apps_user` (
    `id`,
    `system_data_yn`,
    `password`,
    `name`,
    `type`,
    `status`,
    `email`,
    `mobile`,
    `photo`,
    `icon`,
    `profile`,
    `locale`
) values (
    'admin',
    'Y',
    '$2a$10$2IH/ZL5HAw8w.rGStbBunO3jhkoqbNmLOVhe4e8GG3Mzhqg4.f7vi',
    'Administrator',
    'GENERAL',
    'ACTIVE',
    'admin@chomookun.net',
    '010-1234-1234',
    'data:image/png;base64,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',
    'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAACXBIWXMAAAsTAAALEwEAmpwYAAAE/ElEQVRYhbVWa0xTZxh+3kMvFCgCVlkBwU4BCSEDalUwuLiJTueFmGzRxAv+3MZ02cVsi9k0S5b90F2iW/bLKLKpi3NuUdySJc7pGKQXqVABqYoSaSq0oCuU2tPz7geCXHqhgM+vc97v+Z7nyft93zkfYQp4dOV4jiBgPUBZRCwwhLssUq16xbYb0WpRVMZXT+QKxIcBlIegXGagSr18R/OMB+ivO1HBzKcAKCNQRRDtSCjdfnLGAnj+ObYaEGoBxEyGD4CJaHN86fZz0w7w6NKPGkEuNoHw3CTNh9ErEBfEle68H44kRFIRlIG9UzAHgOQAaF8kUtgOPLj0U0KcYrALgHoKAQDAJ8k4PXHpTlcoQtgOxMsGV07DHACUJFKoEwMgRAf+q6t+kRifEEjNYMM0AgCgRoDdDOmAennl3xEDMO8X+ut0nQClTc94AhzxpbcziPZLo4sTlmDgyoLUZ2AOANoh7bGYECAuIPYC8D6DAN4n2uED0Mpdg8Q4OtPuxDhKK3cNjq/LgpHj/IH3+xWCp++hZ41f4sI5yYlTMu1xPwKBbLNT1Bfi/IFPgwYLJ3D287dyTv/+b9u+N7dCN29o+ex3u/DzxatovdWJDK0GgiDg3v0HyFmQgdfWlmFh1tD2ued4gANf/4CKVcsNW/cfMYXyiPgp/nDrupsN1tbswjwdFAo5GhrboMtIBYNxp9MJAMhMmwulQgb7XQeWvpCLx6KIxhu3oS/I7jx4+o/McPpBl2A00tM0e2KahVqL7dZIbeH8NOyprEDvQw8kljA7KRHf1ZxHe0cX6htbh4RjBCyYp90bST/iv+DtQ9UXN768pJroabOuNdvhGfAieVYCZiclYsDrg6XZPmbeqy8t+fWNg8dORdKf1O+YmelAZcVflxqsK4Zr2jkpKNHnQQCh7loLupxPP/dl+nzzZzXnDUTEMxJgOMT3eyu/uFx//QNHd2/QeamaZC4zFHxb9eXx3ZMxjyrAMOxnv6pruN5W0tTaAafrISSWMCclEYW5OiwryjUu3Pzekmj0Im7C0WBmunf+iFhWnIey4rwJ44IQ449GD5hEB5iZzGZzORFt02q1FUr4qd90LiEYV1W8sf8xKSSHw/ELM9csXrz4z0hLETaA2WzewMyHAGTL5XIUFBQAABwNFxDovj2GK2jmI23ZBgBAU1MT/H4/ALQx87sGg6E2lEfQY1hfX59qMplOM/NvALIBQKVSgYhARNAUrQLJR12OY+TQFJWPjMfGxg6P5BLRBbPZfNJqtc6dVACj0bhJJpO1AHh9dF2hUDx9VsYisXj9yLu6aB2UsaqRd6Vy7M2dmbf4/f5Wo9G4KWwAo9G4hYjOAEgeT5SkMfcIJM5Nh/J5AxRZhUjSZoXlPkEyEZ0xGo1bggawWCwlRFSNECfD5/ONMXA6e1BvV/DVm7HsdPYgEAgE5Y6DjIiOWyyWkuECAYDNZkvwer2tANJDzpTJoNPp4HK50NfXh4u1d3D4m2sAgN3vFOGVtTokJSUhJSUFHR0dEEUxlBQA3FepVIvy8/M9AgB4vd6Pw5kDgCiKaG9vh9vthiRJWL0mEwmzAliUH4/y1ZmQJAlutxt2uz2SOQCkDwwMfAQAgtVqjQdQFWnGeLS1udDd7YHN5oTXG9FwAoioymQyxcl8Pl+2IAhnoxVob3fN0+uHdl9LS88NvV7rjlZDkqSc/wH93t0Ac40IGwAAAABJRU5ErkJggg==',
    null,
    null
);
insert into `apps_user` (
    `id`,
    `system_data_yn`,
    `password`,
    `name`,
    `type`,
    `status`,
    `email`,
    `mobile`,
    `photo`,
    `icon`,
    `profile`,
    `locale`
) values (
    'dev',
    'Y',
    '$2a$10$2IH/ZL5HAw8w.rGStbBunO3jhkoqbNmLOVhe4e8GG3Mzhqg4.f7vi',
    'Developer',
    'GENERAL',
    'ACTIVE',
    'dev@chomookun.net',
    '010-3125-1122',
    null,
    null,
    null,
    null
);

-- apps_role
insert into `apps_role` (
    `id`,
    `system_data_yn`,
    `name`,
    `icon`,
    `note`
) values (
    'ADMIN',
    'Y',
    'Administrator Role',
    'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAAXNSR0IArs4c6QAABW5JREFUWEelV2tsVFUQ/ubcu49ui1Gw2m4p5SEQNIYSEUHQUPmh2NYHpixViKIQ/QEBTVCIQFL4owkE0ASpBlCBlNJglFIeiYUGNUIjFASxEbDdPmxpC6HSbvd1zpi7ZZcu+6w9ye6Pe+d8830zc2buISSxTs6erY9JH5YN4kk6YY5ingbCI2AaHthOfBOMqyCqVYxqMP3Z0Hm7Oa+mxp8InhIZOIsLHyPmdQRMZ8ZIAFqCPZKAFqVwGtA25FT8cDmefUwCbUVz032kvcvEawlkSUQ02nsGvCBsNku5JbPiaGc0m6gEWhwFkyV4F4EmJ6E4ETcJ8Hm/kG+PLTv2+73GEQSaHIVPMnCAwKMTIQ/uPTcJCMfI8srTA/eFETCU+8GHBGjU4MCTs2agWSiVn11x5GJwR4jAlaK56RahHwF4anJw/8+KwefMSr0QrIkQAaejsITAHyeTc9J1mJ57Htqo0SBbKsAK3H0L/t/OwHc5JC4WQ8mMjTkHDpcETrDx5yx6+VGQrCOCOa4ukxmmKVNhea0YPOy+CFPuc8G1fhXQ2xMXhsEekxC59rLKejKazLiMtL1gOOKeV4sVlqI3oD01E9AiW4Fy9aJ3z06IC2eTyw1j/7WOnkXUWFQ4RtP4JDNy4u00Qm5+1RHdeU8Pesv3gM6e7g9pEosBJyvKo+bXC15kiUPxci/s2UhZtQ6wRPYj2XMbrq9LQZcvRjrXdcAfvRsT4Jegl6jFUbBZAR/EJG0UxoI3YXomL8JE/tsN177doIt1Ec5F7lTQ/IVQX34Gbvw7KjwRbaKm+fk/gWhWTAI2G1JWrIbIDs8Qe9xw7fwCfOl8pPMZs6Dy54HNFoi6WnDZN9HhmU9Rk6OgHcDDMQnc/wCs76+Blh5uorpvoW/rJ8D1trCt9HgueP4isMUaeE7X/gJKt0WFZ3A7NTny3Yg3bGIQYJ8X7q2fQjVcDYGLJ6ZBveIIOe8ncAUo3RpLnzshAU5NQ4oRAbsxie8uZob37BnImh+BPhfwUAZ43gJwii08IvV/ALu2xybgdBS0U5wU0PARsK5cA/FgehiIx+OB36hwKQGPGzCbwZoe6ej4YVD10TgpSFCEpqefhbn4rdD5Dyj3euHz+RKfdncfuGQ1hIzxYXSnCDcjxjHUxo2H5b2VoNS0kDNDeVLOmcHflUGc+SU2UaZN1OgonKuBDzEQFj+RmQXr0mWgDHsAYFDKDfsL50AVe0FeTywCfmU0okArFnyScbcVi0w7rEuWgzIy4VcKHV03YDWbYDaZEoddSsjqY9B+PgFyu+PYU6OJtLzAMBqbnraXRP8wEvYsWN9ZBsq0Q0qJ611dcHt8SE2xBn7BZUw+eL2Abuofx709oBudkNXHoTc3JpwJirG/wRhGBmBwHGv2LLN1iRH2TEil0N7RCY/PD5vVcG4BEQUq3nfke+iNDSCfp5+AukPAIJE4RjDGsfKL3DEHK+tD9i0fLd9oWbhkrbCPDFOeYrEgzWYNOOf2f6C+/Qp6V0cSbmKaSAJvyC6v2hBoVEEz58F9Y21Tpp/gtGE5IeVG2K13lLe3gnduh+i+NRTnhvron2QGasOOzxf3zZy9o9evzAOVo60VqnQbNFfvUJ07CSgYVV51KQgUkbJLp2qWaiPSt6ekpuqBnLe1gncNXbkCN2mKirIrDtcOVBG1ZpxVlSswYeKH3NlhN5Tr7r6hKJdQVAehFg9UHjMCwRctVVUTfEcPFovO62uGejXzSrll/GCuZgPlNjryJwmm9UyYQUAW7umYUULjZ6CVGb+ypBLjqMULXzLHFsHruabTRDDPYcY0JjWeGIHrORNuEosrRKgFUbXOoj5DmpqpokImyt1/l99dKFnc22gAAAAASUVORK5CYII=',
    'System Administrator Role'
);

-- apps_user_role
insert into `apps_user_role` (
    `user_id`,
    `role_id`
) values (
    'admin',
    'ADMIN'
);

-- apps_authority
insert into `apps_authority` (
    `id`,
    `system_data_yn`,
    `name`,
    `icon`,
    `note`
) values (
    'ADMIN_MONITOR',
    'Y',
    'Administrator Monitor',
    '/static/image/icon-monitor.png',
    'Administrator Monitor Access Authority'
);
insert into `apps_authority` (
    `id`,
    `system_data_yn`,
    `name`,
    `icon`,
    `note`
) values (
    'ADMIN_USER',
    'Y',
    'Administrator User',
    '/static/image/icon-user.png',
    'Administrator Monitor Access Authority'
);
insert into `apps_authority` (
    `id`,
    `system_data_yn`,
    `name`,
    `icon`,
    `note`
) values (
    'ADMIN_USER_EDIT',
    'Y',
    'Administrator User Edit',
    '/static/image/icon-user.png',
    'Administrator User Edit Authority'
);
insert into `apps_authority` (
    `id`,
    `system_data_yn`,
    `name`,
    `icon`,
    `note`
) values (
    'ADMIN_PROPERTY',
    'Y',
    'Administrator Property',
    '/static/image/icon-property.png',
    'Administrator Property Access Authority'
);
insert into `apps_authority` (
    `id`,
    `system_data_yn`,
    `name`,
    `icon`,
    `note`
) values (
    'ADMIN_PROPERTY_EDIT',
    'Y',
    'Administrator Property Edit',
    '/static/image/icon-property.png',
    'Administrator Property Edit Authority'
);

-- apps_role_authority
insert into `apps_role_authority` (
    `role_id`,
    `authority_id`
) values (
    'ADMIN',
    'ADMIN_MONITOR'
);
insert into `apps_role_authority` (
    `role_id`,
    `authority_id`
) values (
    'ADMIN',
    'ADMIN_USER'
);
insert into `apps_role_authority` (
    `role_id`,
    `authority_id`
) values (
    'ADMIN',
    'ADMIN_USER_EDIT'
);
insert into `apps_role_authority` (
    `role_id`,
    `authority_id`
) values (
    'ADMIN',
    'ADMIN_PROPERTY'
);
insert into `apps_role_authority` (
    `role_id`,
    `authority_id`
) values (
    'ADMIN',
    'ADMIN_PROPERTY_EDIT'
);

-- apps_property
insert into `apps_property` (
    `id`,
    `name`,
    `note`,
    `value`
) values (
    'TEST_PROPERTY',
    'Test Property',
    'Property Description',
    'property value'
);

