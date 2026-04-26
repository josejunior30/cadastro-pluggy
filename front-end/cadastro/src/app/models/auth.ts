export interface LoginResponse {
  token: string;
}
export interface JwtPayload {
  exp: number;
  user_name: string;
  authorities: string[];
  jti: string;
  client_id: string;
  scope: string[];
  roles?: string[];
}
